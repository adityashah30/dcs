#Distributed Computing System


##Introduction

* Based on the Non Shared Memory Data Parallel model.


##Usage

* The user chooses one node as the master and the rest of the nodes as slaves.
* The system has been created so that any node can become a master or a slave (one program for both).
* The master takes in the computaton program and the data set and distributes the load to the slaves.
* The estimation of data splitting is done via SIGAR library.
* The master takes the CPU frequency and the load average of the slaves prior to deciding distribution parameters.
* A classic mode has also been provided so as to compare the difference with the new algorithm.


###Master

* Takes in the send, receive ports for communication.
* Takes in a timeout value in seconds for which the server initially broadcasts its address for listening clients to respond to.
* Takes in the paths of Program JAR, Merge JAR and the Data file

####Merge

* After the slaves independently process the files alloted to them by the server, the server needs to combine them back to generate the relevant output.
* This task is taken care of by the Merge JAR either provided by the user or the default Merge JAR that comes with the distribution.
* The default Merge JAR simply appends all the files to one single file.
* If the user wants to create own Merge JAR the API to be followed is
  1. The Merge JAR should take the paths of the files to be merged as command line parameters.
  2. So standalone Merge JAR should function like this
  ```Java
  java -jar Merge.jar file1 file2
  ```
  3. This should merge the files and generate the output in the file pointed to by file1.
  4. The file2 is then deleted by the system.
  5. In the end this yields a single file which contains the merged information of all the files.


###Slave

* The slave takes in the send and recieve ports for communication.
* Please note that the send port of the server should map to the receive port of the slave and vice versa.
* The slave is designed in such a way that once started it listens for broadcast from any server.
* When it detects a broadcast, it sends the current stats of the system to the master.
* Then it waits for the master to send the program file and the data file to the slave.
* On receiving the files, it executed the program and sends back the processed file.
* Upon completion of this, it resumes listening for another broadcast from the server.

##Known Bugs

* The code for logarithmic algorithm is not yet implemented and hence should not be used.
