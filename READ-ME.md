#sonar扫描时不做shading，防止统计不准确
mvn clean install sonar:sonar

#部署时 需要shading
mvn clean install -Pdeploy -Dmaven.test.skip=true