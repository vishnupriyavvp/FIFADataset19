

###Section:1


I will use Hive as datastore for loading data and querying purpose.It can be loaded with daily delta loads using spark framework.The raw data will be transformed and stored in partitioned tables in Reporting layer by spark application using dataframes for delta loads.First create raw table to store the dataset asis.then transform the data and load the data in partitioned tables using spark dataframe.
If delta file arrives then store in raw and the same will be processed by spark application for transformations and write to partitioned table in reporting database in append mode.
Transformations in this e.g such as datatype conversions ,date formatting,value & wage conversions.
 Processed files are Appended to reporting database table is handled in spark code using writetoHiveTable function in scala.

Hive will be easier to query the data for analysis by using partitions & buckets for structured data in Hadoop Distribution


1)create database fifa19_raw;

2)use fifa19_raw;

3)create table fifa19_raw.fifa19_rawtable(
rownum int
, ID  int 
, Name   string 
, Age  int 
, Photo   string 
, Nationality   string 
, Flag   string 
, Overall  int 
, Potential  int 
, Club   string 
, Club_Logo   string 
, Value   string 
, Wage   string 
, Special  int 
, Preferred_Foot   string 
, International_Reputation  int 
, Weak_Foot  int 
, Skill_Moves  int 
, Work_Rate   string 
, Body_Type   string 
, Real_Face   string 
, Position   string 
, Jersey_Number  int 
, Joined   string 
, Loaned_From   string 
, Contract_Valid_Until   string 
, Height   string 
, Weight   string 
, LS   string 
, ST   string 
, RS   string 
, LW   string 
, LF   string 
, CF   string 
, RF   string 
, RW   string 
, LAM   string 
, CAM   string 
, RAM   string 
, LM   string 
, LCM   string 
, CM   string 
, RCM   string 
, RM   string 
, LWB   string 
, LDM   string 
, CDM   string 
, RDM   string 
, RWB   string 
, LB   string 
, LCB   string 
, CB   string 
, RCB   string 
, RB   string 
, Crossing  int 
, Finishing  int 
, HeadingAccuracy  int 
, ShortPassing  int 
, Volleys  int 
, Dribbling  int 
, Curve  int 
, FKAccuracy  int 
, LongPassing  int 
, BallControl  int 
, Acceleration  int 
, SprintSpeed  int 
, Agility  int 
, Reactions  int 
, Balance  int 
, ShotPower  int 
, Jumping  int 
, Stamina  int 
, Strength  int 
, LongShots  int 
, Aggression  int 
, Interceptions  int 
, Positioning  int 
, Vision  int 
, Penalties  int 
, Composure  int 
, Marking  int 
, StandingTackle  int 
, SlidingTackle  int 
, GKDiving  int 
, GKHandling  int 
, GKKicking  int 
, GKPositioning  int 
, GKReflexes  int 
, Release_Clause   string 
) 
row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
WITH SERDEPROPERTIES (
"seperatorChar" ="\,",
"quoteChar" ="\""
)
location '/hive/warehouse/fifadataset' ;



4)load data local inpath '/home/vishnu/Downloads/73041_220332_compressed_data.csv/data.csv' into table fifa19_raw.fifa19_rawtable;

###section2:
please change the input data file in the scala pobject <src/main/scala/FifaConstants.scala> to the path where the input file data.csv resides in the system to execute the code
val inputfile=<Absolute Path of the Input file>


###Section3:

1)Build Dockerfile: Git has the docker file which contains all the instructions required to install the dependencies to execute the postgres sql and spark.Also copied the project code to docker file from git clone command to build the image.

2)Build Docker Image:It will create an image and tags it as "fifa19:1.0" from the above docker file.
docker build --tag fifa19:1.0 .

3)Run Docker Image:Tells the docker to run the container named "vishnu" using the tag created for the project 
docker run --publish 8000:8080 --detach --name vishnu fifa19:1.0

4)To check the container running in docker
docker ps --all










