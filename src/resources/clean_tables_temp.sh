#!/bin/bash
db="Workflow"
user="mhachet"
pass="ledzeppelin"
directory="/home/mhachet/workspace/WebWorkflowCleanData/"


#find  $directory"WebContent/output/temp/" -name "*" -type d -mtime +3 -exec rm -rf  {} \; 
rm -R $directory"WebContent/output/temp/"

mysql -u$user -p$pass -e "SHOW TABLES FROM $db" >$directory"src/resources/showTable.txt"

for line in $(cat /home/mhachet/workspace/WebWorkflowCleanData/src/resources/showTable.txt); 
	do 
	if [ $line != "DarwinCoreInput" ] && [ $line != "IsoCode" ] && [ $line != "TaxIso" ] && [ $line != "Taxon" ] && [ $line != "Tables_in_Workflow" ];
		then mysql -u$user -p$pass -e "DROP TABLE Workflow."$line
	elif [ $line = "DarwinCoreInput" ]; 
		then mysql -u$user -p$pass -e "DELETE FROM Workflow."$line
	fi
	done; 


