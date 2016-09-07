package org.zooffice.infra.config;

import javax.persistence.Entity;

import org.apache.commons.dbcp.BasicDataSource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.zooffice.common.constant.ZoofficeConstants;
import org.zooffice.common.util.PropertiesWrapper;
import org.zooffice.infra.logger.CoreLogger;

/**
 * Dynamic datasource bean configuration.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Configuration
public class DatabaseConfig implements ZoofficeConstants {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

	@Autowired
	private Config config;

	/**
	 * Create the dataSource based on the database configuration.
	 * 
	 * @return dataSource
	 */
	@Bean(name = "dataSource", destroyMethod = "close")
	public BasicDataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		PropertiesWrapper databaseProperties = config.getDatabaseProperties();
		Database database = Database.getDatabase(databaseProperties.getProperty("database", "H2",
						"[FATAL] Database type is not sepecfied. In default, use H2."));
		database.setup(dataSource, databaseProperties);
		return dataSource;
	}

	/**
	 * Create {@link LocalContainerEntityManagerFactoryBean} bean for Hibernate. Hibernate doesn't
	 * support the search for the {@link Entity} classes in the other Jar files. This method
	 * directly searches the {@link Entity} classes with {@link Reflections} not using Hibernate
	 * entity class search feature to overcome the limitation
	 * 
	 * use annotation DependsOn to insure after databaseUpdater is
	 * 
	 * @return {@link LocalContainerEntityManagerFactoryBean}
	 */
	@Bean(name = "emf")
	@DependsOn("databaseUpdater")
	public LocalContainerEntityManagerFactoryBean emf() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPersistenceUnitName("zooffice");
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		PropertiesWrapper databaseProperties = config.getDatabaseProperties();

		Database database = Database.getDatabase(databaseProperties.getProperty("database", "H2",
						"[FATAL] Database type is not sepecfied. In default, use H2."));
		if (config.isCluster() && !database.isClusterSupport()) {
			CoreLogger.LOGGER.error("In cluster mode, H2 is not allowed to use. Please select cubrid as database");
		}
		hibernateJpaVendorAdapter.setDatabasePlatform(database.getDialect());
		hibernateJpaVendorAdapter.setShowSql(false);
		emf.setJpaVendorAdapter(hibernateJpaVendorAdapter);
		// To search entity packages from other jar files..
		emf.setPackagesToScan("empty");
		emf.setPersistenceUnitPostProcessors(new PersistenceUnitPostProcessor() {
			@Override
			public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
				Reflections reflections = new Reflections(ZOOFFICE_DEFAULT_PACKAGE);
				for (Class<?> each : reflections.getTypesAnnotatedWith(Entity.class)) {
					LOGGER.trace("Entity class {} is detected as the SpringData entity.", each.getName());
					pui.addManagedClassName(each.getName());
				}
			}
		});
		return emf;
	}

	/**
	 * Create the transactionManager.
	 * 
	 * @return {@link JpaTransactionManager}
	 */
	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf().getObject());
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}
}
