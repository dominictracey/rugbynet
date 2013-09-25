package net.rugby.foundation.admin.shared;

import java.util.Date;

public interface IRatingEngineSchema {
	public abstract Long getId();
	public abstract String getName();
	public abstract String getDescription();
	public abstract String getChangeLog();
	public abstract void setDescription(String description);
	public abstract void setChangeLog(String changeLog);
	public abstract void setId(Long id);
	public abstract void setName(String schemaName);
	public abstract Boolean getIsDefault();
	public abstract void setIsDefault(Boolean isDefault);
	public abstract void setCreated(Date date);
	public abstract Date getCreated();
}
