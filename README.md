# distributed-system-cpu-task-scheduler
![workflow](https://github.com/Neville-Loh/distributed-system-scheduler/actions/workflows/maven.yml/badge.svg)

The project is to develop a branch-and-bound, or A* type algorithm that solves the NP Hard Scheduling problem with the highest possible performance.
The scheduling problem is depicted as a weighted directed acyclic graph where the nodes are tasks and its weight is the cost of computation, 
and the edges are the communication costs between tasks. Now with the commonality of multi-processing, to be able to achieve the highest 
possible performance, the algorithm will need to be parallelized, so that the algorithm itself is able to use multiple processes to speed up the search.

# Getting Started
Maven 4.0.0 is required to compile the project.

## Maven install
please refer to [Maven Install Instruction](https://maven.apache.org/download.cgi) for direct download.

#### Linux
```bash
apt install maven
```

#### Mac (brew)
```
brew install maven
```

## building and packaging into Jar with dependency
```
git clone git@github.com:SoftEng306-2021/project-1-raspberry-spirits-15.git
mvn clean package -DskipTests
```

The compiled jar will be located in `target/scheduler.jar`


## Running Jar
The compiled Jar file can be run using the following command.
```bash
Java -jar target/scheduler.jar INPUT.dot P [OPTION]
```

### Arguments
`INPUT.dot`   a  task  graph  with  integer  weights  in  dot  format  
`P` number  of  processors  to  schedule  the INPUT graph on


Notice users must have java version of at least `Java 1.8`.
### Optional Arguments
`−o` OUTPUT output file is named OUTPUT (default is INPUT−output.dot )  
`−p N` use N cores for execution in parallel (default is sequential)  
`-v` visualise the serach







# Useful Documentation
- [Meeting minutes](https://github.com/SoftEng306-2021/project-1-raspberry-spirits-15/wiki/Meeting-minutes)
- [Changelogs](https://github.com/SoftEng306-2021/project-1-raspberry-spirits-15/wiki/Changelog)

