# raspberry-spirits-15-scheduler
![workflow](https://github.com/SoftEng306-2021/project-1-raspberry-spirits-15/actions/workflows/maven.yml/badge.svg)

#Getting Started



## Maven install
please refer to [Maven Install Instruction](https://maven.apache.org/download.cgi) for direct download.


### Linux
```bash
apt install maven
```

### Mac (brew)
```
brew install maven
```

## building and packaging into Jar with dependency
```
git clone git@github.com:SoftEng306-2021/project-1-raspberry-spirits-15.git
mvn clean package
```

The compiled jar will be located in `/target/scheduler-1.0.0-jar-with-dependencies.jar`


## Running Jar
```bash
Java -jar /target/scheduler-1.0.0-jar-with-dependencies.jar INPUT. dot P [OPTION]
```
###Optional arguments
```
−o OUPUT output  file is named OUTPUT (default is INPUT−output.dot )
```



