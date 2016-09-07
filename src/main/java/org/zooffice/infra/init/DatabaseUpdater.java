
package org.zooffice.infra.init;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.H2ExTypeConverter;
import liquibase.database.jvm.JdbcConnection;
import liquibase.database.typeconversion.TypeConverterFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.sqlgenerator.core.ModifyDataTypeGenerator;
import liquibase.sqlgenerator.core.RenameColumnGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.zooffice.common.excpetion.ZoofficeRuntimeException;

/**
 * DB Data Updater. This class is used to update DB automatically when System restarted through log
 * file db.changelog.xml
 * 
 * @author Matt
 * @author JunHo Yoon
 * @since 3.0
 */
@Service
@DependsOn("dataSource")
public class DatabaseUpdater implements ResourceLoaderAware {

	@Autowired
	private DataSource dataSource;

	private String changeLog = "datachange_logfile/db.changelog.xml";

	private String contexts;

	private ResourceLoader resourceLoader;

	private Database getDatabase() {
		try {
			Database databaseImplementation = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
							new JdbcConnection(dataSource.getConnection()));
			return databaseImplementation;
		} catch (Exception e) {
			throw new ZoofficeRuntimeException("Error getting database", e);
		}
	}

	public String getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	/**
	 * Automated updates DB after nGrinder has load with all bean properties.
	 * 
	 * @throws Exception
	 *             occurs when db update is failed.
	 */
	@PostConstruct
	public void init() throws Exception {
		SqlGeneratorFactory.getInstance().register(new LockExDatabaseChangeLogGenerator());
		TypeConverterFactory.getInstance().register(H2ExTypeConverter.class);
		LiquibaseEx liquibase = new LiquibaseEx(getChangeLog(), new ClassLoaderResourceAccessor(getResourceLoader()
						.getClassLoader()), getDatabase());
		// previous RenameColumnGenerator don't support Cubrid,so remove it and add new Generator
		SqlGeneratorFactory.getInstance().unregister(RenameColumnGenerator.class);
		SqlGeneratorFactory.getInstance().register(new RenameColumnExGenerator());
		SqlGeneratorFactory.getInstance().unregister(ModifyDataTypeGenerator.class);
		SqlGeneratorFactory.getInstance().register(new ModifyDataTypeExGenerator());
		try {
			liquibase.update(contexts);
		} catch (LiquibaseException e) {
			throw new ZoofficeRuntimeException("Exception occurs while Liquibase update DB", e);
		}
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
