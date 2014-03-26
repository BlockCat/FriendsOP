package me.blockcat.friendsop.Pool;

public interface Pool<Type> {
	public void recycle(final Type data);
	public Type get();   
	public void setFactory(final PoolObjectFactory<Type> factory);
	public void reset();
	public String debug();
}
