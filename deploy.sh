if [ "$1" = "dev" ]; then
  export host="wmcollective.org"
  export user="ec2-user"
  export pemfile="~/personal.pem"
else
  echo "Please specify 'dev'"
  exit 1
fi

echo "Deploying to $user@$host"

gradle -Penv="$1" --warning-mode all build
if [ $? -ne 0 ]; then
  echo "Build failed!"
  exit 1
fi

rsync -Pav -e "ssh -i $pemfile" build/libs/temporary.war $user@$host:~/temporary.war
if [ $? -ne 0 ]; then
  echo "Upload failed!"
  exit 1
fi

if [ "$1" = "dev" ]; then
  ssh -i $pemfile $user@$host "sudo tomcatdown"
  ssh -i $pemfile $user@$host "sudo rm -rf /usr/share/tomcat/webapps/temporary"
  ssh -i $pemfile $user@$host "sudo cp temporary.war /usr/share/tomcat/webapps/temporary.war"
  ssh -i $pemfile $user@$host "sudo tomcatup"
#  ssh -i $pemfile $user@$host "rm temporary.war"
  ssh -i $pemfile $user@$host "sudo tail -f /usr/share/tomcat/logs/catalina.out"
fi