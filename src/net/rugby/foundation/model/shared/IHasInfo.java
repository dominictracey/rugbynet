package net.rugby.foundation.model.shared;

import java.util.Date;

public interface IHasInfo {

	public abstract Date getCreatedDate();

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#setCreatedDate(java.util.Date)
	 */

	public abstract void setCreatedDate(Date createdDate);

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#getLastModifiedDate()
	 */

	public abstract Date getLastModifiedDate();

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#setLastModifiedDate(java.util.Date)
	 */

	public abstract void setLastModifiedDate(Date lastModifiedDate);

}