#!/bin/bash
db="Workflow"
user="mhachet"
pass="ledzeppelin"
directory="/var/lib/tomcat7/webapps/WebWorkflowCleanData/"

mysql -u$user -p$pass -e "SHOW TABLES FROM $db" >$directory"src/resources/showTable.txt"

for line in $(cat /home/mhachet/workspace/WebWorkflowCleanData/src/resources/showTable.txt); 
	do 
	if [ $line != "DarwinCoreInput" ] && [ $line != "IsoCode" ] && [ $line != "TaxIso" ] && [ $line != "Taxon" ] && [ $line != "Tables_in_Workflow" ];
		then mysql -u$user -p$pass -e "DROP TABLE Workflow."$line
	elif [ $line = "DarwinCoreInput" ]; 
		then mysql -u$user -p$pass -e "DELETE FROM Workflow."$line
	fi
	done; 


