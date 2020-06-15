import org.apache.spark.sql.functions.{col, from_unixtime, to_date, unix_timestamp}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object FifaMain {

  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder().appName("FIFA19Analysis").master("local[*]")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("spark.sql.warehouse.dir", "hdfs://localhost:9000/hive/warehouse").enableHiveSupport().getOrCreate();
//Read csv file
    val d = ss.read.option("header",true).option("inferSchema",true).csv(FifaConstants.inputfile)
//replaces space in column name of the dataframe
    var newDf: DataFrame =  d
    newDf.columns.foreach { col =>
       newDf = newDf.withColumnRenamed(col, col.replaceAll(" ", ""))}
//Register udf in spark and convert wage,value and joined to double and date respectively
    val reg = ss.udf.register("fconvertWageorvalueToDec",FifaFunctions.convertWageorvalueToDouble(_:String))
    val d1 =  newDf.withColumn("Wage",reg(col("Wage")) ).withColumn("Value",reg(col("Value")) ).withColumn("Joined", to_date(from_unixtime(unix_timestamp(col("Joined"),"MMM dd, yyyy"))) )

//Question1 Left Mid Footer under age 30
    FifaFunctions.leftFootedNumber(ss,d1,30)

//Question2 Strongest team
    FifaFunctions.strongestTeam(ss,d1)

  //Question3 Expensive Squad
    FifaFunctions.expensiveSquadvaluewageComparision(ss,d1)

   //Question4 Position highest wage average
   val dh = d1.groupBy("Position").agg(avg("Wage").alias("avg")).sort(desc("avg")).show(1)
//Question5 Great Goalkeeper
    FifaFunctions.bestGoalKeeper(ss,d1)

    //Question6 Best striker
    FifaFunctions.bestStriker(ss,d1)

   //To store the data in Hive for section 1 load the transformed data from raw layer using spark dataframe.
    FifaFunctions.writetoHiveTable(d1,FifaConstants.Hive_table)

 //Section3 to store the dataset in Postgres
    FifaFunctions.writetoPostgres(d1,FifaConstants.Postgres_table)

  }
}