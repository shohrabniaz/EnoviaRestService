#!/bin/bash
# Author Md. Omour Faruq on 20-02-2019
while IFS='' read -r line || [[ -n "$line" ]]; do
printf  "\n\n"
echo "------------------------------------ ||| Installing Jar ||| ------------------------------------"
echo "$line"
echo "------------------------------------ ||| Installing Jar ||| ------------------------------------"
    eval $line
done < "lib/MvnCommands.txt"

printf  "\n\n"
echo "------------------------------------ ||| Building Application ||| ------------------------------------"
#mvn clean install -P dev4
mvn clean install

# Modified by Omour Faruq on 12-11-2019
PROPERTY_FILE="src/main/resources/application-dev4.properties"

function getProperty() {
   PROP_KEY=$1
   echo "Key: '$PROP_KEY'"
   PROP_VALUE=$(cat $PROPERTY_FILE | grep $PROP_KEY | cut -d'=' -f2)
   echo "Value: '$PROP_VALUE'"
}

echo "# Reading property from '$PROPERTY_FILE'"
REPOSITORY_URL=$(getProperty "bom.import.mapping.xml.directory")