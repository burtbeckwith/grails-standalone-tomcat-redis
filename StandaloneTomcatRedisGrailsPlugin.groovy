import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.zinin.redis.session.RedisManager

class StandaloneTomcatRedisGrailsPlugin {

	private final Logger log = LoggerFactory.getLogger('grails.plugin.standalone.StandaloneTomcatRedisGrailsPlugin')

	String version = '0.3'
	String grailsVersion = '2.0 > *'
	List pluginExcludes = [
		'docs/**',
		'src/docs/**'
	]

	String title = 'Standalone Tomcat Redis Plugin'
	String author = 'Burt Beckwith'
	String authorEmail = 'burt@burtbeckwith.com'
	String description = 'Uses Redis as the Tomcat session manager when using the Tomcat server'
	String documentation = 'http://grails.org/plugin/standalone-tomcat-redis'

	String license = 'APACHE'
	def issueManagement = [system: 'GitHub', url: 'https://github.com/burtbeckwith/grails-standalone-tomcat-redis/issues']
	def scm = [url: 'https://github.com/burtbeckwith/grails-standalone-tomcat-redis']

	def doWithSpring = {
		def conf = application.config.grails.plugin.standalone.tomcat.redis

		tomcatSessionManager(RedisManager) {
			disableListeners = true
			if (conf.dbIndex)       dbIndex       = conf.dbIndex
			if (conf.redisHostname) redisHostname = conf.redisHostname
			if (conf.redisPassword) redisPassword = conf.redisPassword
			if (conf.redisPort)     redisPort     = conf.redisPort
		}
	}

	def doWithApplicationContext = { ctx ->

		def tomcatSessionManager = ctx.tomcatSessionManager
		if (!tomcatSessionManager) {
			log.debug "No tomcatSessionManager bean found, not updating the Tomcat session manager"
			return
		}

		def servletContext = ctx.servletContext

		try {
			if ('org.apache.catalina.core.ApplicationContextFacade'.equals(servletContext.getClass().name)) {
				def realContext = servletContext.context
				if ('org.apache.catalina.core.ApplicationContext'.equals(realContext.getClass().name)) {
					def standardContext = realContext.@context
					if ('org.apache.catalina.core.StandardContext'.equals(standardContext.getClass().name)) {
						standardContext.manager = tomcatSessionManager
						log.info "Set the Tomcat session manager to $tomcatSessionManager"
					}
					else {
						log.warn "Not updating the Tomcat session manager, the context isn't an instance of org.apache.catalina.core.StandardContext"
					}
				}
				else {
					log.warn "Not updating the Tomcat session manager, the wrapped servlet context isn't an instance of org.apache.catalina.core.ApplicationContext"
				}
			}
			else {
				log.warn "Not updating the Tomcat session manager, the servlet context isn't an instance of org.apache.catalina.core.ApplicationContextFacade"
			}
		}
		catch (Throwable e) {
			log.error "There was a problem changing the Tomcat session manager: $e.message", e
		}
	}
}
