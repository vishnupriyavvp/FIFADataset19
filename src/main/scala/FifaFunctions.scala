import java.util.Properties

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, count, dense_rank, desc, sum}
import org.apache.spark.sql.{DataFrame, SparkSession}


object FifaFunctions {

  def convertWageorvalueToDouble(wageOrValue: String): Double= {

    val length  = wageOrValue.length()
    if (wageOrValue.slice(1,length) =="0"){
           return 0
    }
    var exp = wageOrValue(length-1)
       val fin = wageOrValue.slice(1,length-1)
       var fi = fin.toDouble
    if(exp == 'K' || exp =='k' )    {
            fi = fi*1000
    }
    else if (exp == 'M' || exp =='m') {
            fi = fi*1000000
    }
    return fi.toDouble
  }
def leftFootedNumber(spark:SparkSession,d1:DataFrame,year:Int):Unit= {

  val df = d1.filter(col("Position").isin(FifaConstants.leftMid: _*) and col("age") < year)
  val df2 = df.filter(col("Club").isNotNull).groupBy("Club").agg(count("ID") alias ("count")).sort(desc("count")).select(col("club")).show(2)
}
def strongestTeam(spark:SparkSession,d1:DataFrame):Unit= {
  val pwf = d1.filter(col("Position") isin (FifaConstants.PositionList: _*))
  val wf = Window.partitionBy("position").orderBy(desc("overall"))
  val st = pwf.withColumn("rank", dense_rank().over(wf))
  st.filter(col("rank") < 2).select("name", "nationality", "club", "value", "wage", "overall", "position", "rank").show()
}
def expensiveSquadvaluewageComparision(spark:SparkSession,d1:DataFrame):Unit= {
  val dw = d1.groupBy("Club").agg(sum("Value").alias("val")).sort(desc("val")).take(1)
  val de = d1.groupBy("Club").agg(sum("Wage").alias("val")).sort(desc("val")).take(1)
  val y = dw.toList
  val z = de.toList
  val x = y(0)(0).equals(z(0)(0))
  println("Most expensive squad value and wage bill is same: " + x)

}

  def bestGoalKeeper(spark:SparkSession,d1:DataFrame):Unit=  {
    val GKdf = d1.withColumn("GKTotalScore", (col("GKDiving") + col("GKHandling") + col("GKKicking") + col("GKPositioning") + col("GKReflexes")))
    GKdf.sort(desc("GKTotalScore")).select("name", "GKDiving", "GKHandling", "GKKicking", "GKPositioning", "GKReflexes").show(1)

}
  def bestStriker(spark:SparkSession,d1:DataFrame):Unit= {
    val STdf = d1.withColumn("STTotalScore", (col("BallControl") + col("SprintSpeed") + col("Positioning") + col("Crossing") + col("Dribbling")))
    STdf.sort(desc("STTotalScore")).select("name", "BallControl", "SprintSpeed", "Positioning", "Crossing", "Dribbling").show(1)
  }

   def writetoHiveTable(d1:DataFrame,tablename:String):Unit= {
    d1.write.mode("append")
      .partitionBy("Club").saveAsTable(tablename)

  }

  def writetoPostgres(d1:DataFrame,tablename:String):Unit= {
    val url = FifaConstants.url
    val driver = FifaConstants.driver
    val cp = new Properties()
    cp.setProperty("Driver", driver)
    cp.setProperty("user","postgres")
    cp.setProperty("password","postgres")
    d1.write.mode("append").jdbc(url=url,table=FifaConstants.Postgres_table,connectionProperties = cp)
  }
}
