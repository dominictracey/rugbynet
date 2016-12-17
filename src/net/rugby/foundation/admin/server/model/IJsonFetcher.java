package net.rugby.foundation.admin.server.model;

import java.util.List;

public interface IJsonFetcher {

	String getErrorMessage();

	String getErrorCode();

	List<String> getWarningCodes();

	List<String> getWarningMessages();

}