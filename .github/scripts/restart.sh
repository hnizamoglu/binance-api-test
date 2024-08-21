processId=$(ps -ef | awk '/[j]ava -jar/{print $2}\')
if [ ! $processId ]; then
	echo "No process found!"
else
	kill $processId
	echo "App process killed."
fi

if [ ! -f ./app.jar ]; then
	echo "Jar file not found."
else
	rm -f app.jar
	echo "JAR file removed."
fi

echo "Downloading JAR file from S3"
aws s3 cp s3://trader-artifact-bucket/app.jar .
echo "Download complete."

echo "Launching new version..."
java -jar app.jar < /dev/null 2> /dev/null > /dev/null &