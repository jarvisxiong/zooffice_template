
package org.zooffice.infra.init;

import liquibase.database.Database;
import liquibase.database.core.CUBRIDDatabase;
import liquibase.database.core.DerbyDatabase;
import liquibase.database.core.FirebirdDatabase;
import liquibase.database.core.H2Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.core.InformixDatabase;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MaxDBDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.SybaseASADatabase;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.RenameColumnGenerator;
import liquibase.statement.core.RenameColumnStatement;

/**
 * Rename Column sql generator. Modified to support Cubrid.
 * 
 * @since 3.1
 * @author Matt
 */
public class RenameColumnExGenerator extends RenameColumnGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see liquibase.sqlgenerator.core.RenameColumnGenerator#generateSql(liquibase.statement.core.
	 * RenameColumnStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(RenameColumnStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		String sql;
		if (database instanceof MSSQLDatabase) {
			sql = "exec sp_rename '"
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ "."
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName()) + "', '" + statement.getNewColumnName() + "'";
		} else if (database instanceof MySQLDatabase) {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " CHANGE "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName()) + " " + statement.getColumnDataType();
		} else if (database instanceof HsqlDatabase || database instanceof H2Database) {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " ALTER COLUMN "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " RENAME TO "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		} else if (database instanceof FirebirdDatabase) {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " ALTER COLUMN "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " TO "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		} else if ((database instanceof MaxDBDatabase) || (database instanceof DerbyDatabase)
						|| (database instanceof InformixDatabase)) {
			sql = "RENAME COLUMN "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ "."
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " TO "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		} else if (database instanceof SybaseASADatabase) {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " RENAME "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " TO "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		} else if (database instanceof CUBRIDDatabase) {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " RENAME COLUMN "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " AS "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		} else {
			sql = "ALTER TABLE "
							+ database.escapeTableName(statement.getSchemaName(), statement.getTableName())
							+ " RENAME COLUMN "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getOldColumnName())
							+ " TO "
							+ database.escapeColumnName(statement.getSchemaName(), statement.getTableName(),
											statement.getNewColumnName());
		}
		return new Sql[] { new UnparsedSql(sql) };
	}
}
