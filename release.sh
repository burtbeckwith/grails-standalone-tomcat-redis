rm -rf target/release
mkdir target/release
cd target/release
git clone git@github.com:burtbeckwith/grails-standalone-tomcat-redis.git
cd grails-standalone-tomcat-redis
grails clean
grails compile
#grails publish-plugin --noScm --snapshot --stacktrace
grails publish-plugin --noScm --stacktrace
