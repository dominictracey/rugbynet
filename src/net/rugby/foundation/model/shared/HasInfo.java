package net.rugby.foundation.model.shared;

import java.util.Date;

public abstract class HasInfo implements IHasInfo  {

	private Date createdDate;
	private Date lastModifiedDate;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasInfo#getCreatedDate()
	 */
	@Override
	public Date getCreatedDate() {
		return createdDate;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#setCreatedDate(java.util.Date)
	 */

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasInfo#setCreatedDate(java.util.Date)
	 */
	@Override
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#getLastModifiedDate()
	 */

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasInfo#getLastModifiedDate()
	 */
	@Override
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasId#setLastModifiedDate(java.util.Date)
	 */

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IHasInfo#setLastModifiedDate(java.util.Date)
	 */
	@Override
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
