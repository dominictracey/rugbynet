package net.rugby.foundation.model.shared.fantasy;

import java.util.List;

public interface Constraint<T,P> {
	void setParameter(T val);
	boolean canAdd(P val, List<P> list);
	void setMax(int max);
	void setMin(int min);
	boolean validate(List<P> currentList);
	String getError();
	String getDetails();
}
