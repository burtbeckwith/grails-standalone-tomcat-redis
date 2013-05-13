grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile 'ru.zinin:tomcat-redis-session:0.5', {
			excludes 'commons-pool', 'jedis', 'tomcat-catalina', 'tomcat-jasper', 'tomcat-servlet-api'
		}
		compile 'redis.clients:jedis:2.1.0', {
			excludes 'commons-pool', 'junit'
		}
	}

	plugins {
		build(':release:2.2.1', ':rest-client-builder:1.0.3') {
			export = false
		}
	}
}
