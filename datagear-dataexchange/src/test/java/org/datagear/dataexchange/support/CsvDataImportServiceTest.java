/*
 * Copyright (c) 2018 datagear.org. All Rights Reserved.
 */

package org.datagear.dataexchange.support;

import java.io.Reader;
import java.sql.Connection;

import org.datagear.connection.IOUtil;
import org.datagear.connection.JdbcUtil;
import org.datagear.dataexchange.DataFormat;
import org.datagear.dataexchange.DataexchangeTestSupport;
import org.datagear.dataexchange.ExceptionResolve;
import org.datagear.dataexchange.SimpleConnectionFactory;
import org.datagear.dataexchange.TextDataImportOption;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@linkplain CsvDataImporter}单元测试类。
 * 
 * @author datagear@163.com
 *
 */
public class CsvDataImportServiceTest extends DataexchangeTestSupport
{
	public static final String TABLE_NAME = "T_DATA_IMPORT";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private CsvDataImportService csvDataImportService;

	public CsvDataImportServiceTest()
	{
		super();
		this.csvDataImportService = new CsvDataImportService(databaseInfoResolver);
	}

	@Test
	public void exchangeTest_ignoreInexistentColumn_false() throws Exception
	{
		DataFormat dataFormat = new DataFormat();

		Connection cn = getConnection();
		Reader reader = null;

		try
		{
			cn = getConnection();
			reader = IOUtil.getReader(getTestResourceInputStream("CsvDataImporterTest_ignoreInexistentColumn.csv"),
					"UTF-8");

			TextDataImportOption textDataImportOption = new TextDataImportOption(false, ExceptionResolve.ABORT, true);
			CsvDataImport impt = new CsvDataImport(new SimpleConnectionFactory(cn, false), dataFormat,
					textDataImportOption, TABLE_NAME, reader);

			clearTable(cn, TABLE_NAME);

			this.thrown.expect(ColumnNotFoundException.class);
			this.thrown.expectMessage("Column [INEXISTENT_COLUMN] not found");

			this.csvDataImportService.exchange(impt);
		}
		finally
		{
			JdbcUtil.closeConnection(cn);
			IOUtil.close(reader);
		}
	}

	@Test
	public void exchangeTest_ignoreInexistentColumn_true() throws Exception
	{
		DataFormat dataFormat = new DataFormat();

		Connection cn = null;
		Reader reader = null;

		try
		{
			cn = getConnection();
			reader = IOUtil.getReader(getTestResourceInputStream("CsvDataImporterTest_ignoreInexistentColumn.csv"),
					"UTF-8");

			TextDataImportOption textDataImportOption = new TextDataImportOption(true, ExceptionResolve.ABORT, true);
			CsvDataImport impt = new CsvDataImport(new SimpleConnectionFactory(cn, false), dataFormat,
					textDataImportOption, TABLE_NAME, reader);

			clearTable(cn, TABLE_NAME);

			this.csvDataImportService.exchange(impt);

			int count = getCount(cn, TABLE_NAME);

			Assert.assertEquals(3, count);
		}
		finally
		{
			JdbcUtil.closeConnection(cn);
			IOUtil.close(reader);
		}
	}

	@Test
	public void exchangeTest_ExceptionResolve_ignore() throws Exception
	{
		DataFormat dataFormat = new DataFormat();

		Connection cn = null;
		Reader reader = null;

		try
		{
			cn = getConnection();
			reader = IOUtil.getReader(getTestResourceInputStream("CsvDataImporterTest__ExceptionResolve.csv"), "UTF-8");

			TextDataImportOption textDataImportOption = new TextDataImportOption(true, ExceptionResolve.IGNORE, true);
			CsvDataImport impt = new CsvDataImport(new SimpleConnectionFactory(cn, false), dataFormat,
					textDataImportOption, TABLE_NAME, reader);

			clearTable(cn, TABLE_NAME);

			this.csvDataImportService.exchange(impt);

			int count = getCount(cn, TABLE_NAME);

			Assert.assertEquals(2, count);
		}
		finally
		{
			JdbcUtil.closeConnection(cn);
			IOUtil.close(reader);
		}
	}

	@Test
	public void exchangeTest_ExceptionResolve_abort() throws Exception
	{
		DataFormat dataFormat = new DataFormat();

		Connection cn = null;
		Reader reader = null;

		try
		{
			cn = getConnection();
			reader = IOUtil.getReader(getTestResourceInputStream("CsvDataImporterTest__ExceptionResolve.csv"), "UTF-8");

			TextDataImportOption textDataImportOption = new TextDataImportOption(true, ExceptionResolve.ABORT, false);
			CsvDataImport impt = new CsvDataImport(new SimpleConnectionFactory(cn, false), dataFormat,
					textDataImportOption, TABLE_NAME, reader);

			clearTable(cn, TABLE_NAME);

			this.thrown.expect(IllegalSourceValueException.class);

			this.csvDataImportService.exchange(impt);
		}
		finally
		{
			JdbcUtil.closeConnection(cn);
			IOUtil.close(reader);
		}
	}
}