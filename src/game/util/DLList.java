package game.util;

import java.lang.reflect.Array;

public class DLList<E> {
	
	private Node<E> dummy;
	private int size = 0;
	
	public DLList() {
		dummy = new Node<E>(null);
		dummy.setNext(dummy);
		dummy.setPrev(dummy);
	}
	
	private Node<E> getNode(int idx) {
		Node<E> current = dummy;
		
		if(idx <= size/2) {
			for(int i=0; i < size; i++) {
				current = current.next();
				if(i == idx)
					return current;
			}
		}
		else {
			for(int i = size-1; i >= 0; i--) {
				current = current.prev();
				if(i == idx)
					return current;
			}
		}
		
		return null;
	}
	
	private Node<E> getNode(E e) {
		Node<E> cur = dummy.next();
		for(int i = 0; i < size; i++) {
			if(cur.get().equals(e))
				return cur;
			cur = cur.next();
		}
		
		return null;
	}
	
	public void add(Node<E> afterNew, E e) {
		Node<E> newNode = new Node<E>(afterNew.prev(), e, afterNew);
		afterNew.prev().setNext(newNode);
		afterNew.setPrev(newNode);
		size++;
	}
	public void add(E e) { add(dummy, e); }
	public void add(E e, int idx) { add(idx==size?dummy:getNode(idx), e); }
	
	private E remove(Node<E> node) {
		if(node == null) return null;
		node.prev().setNext(node.next());
		node.next().setPrev(node.prev());
		size--;
		return node.get();
	}
	public E remove(int idx) { return remove(getNode(idx)); }
	public void remove(E e) { remove(getNode(e)); }
	
	public E get(int idx) {
		Node<E> node = getNode(idx);
		return node == null ? null : node.get();
	}
	
	public void set(int idx, E e) {
		Node<E> node = getNode(idx);
		if(node != null) node.set(e);
		else System.err.println("failed to set node " + idx + " to " + e);
	}
	
	public int size() { return size; }
	public boolean contains(E e) { return getNode(e) != null; }
	
	public void scramble() {
		for(int i = 0; i < size; i++) {
			int ranIdx = (int) (Math.random() * size);
			swap(getNode(i), getNode(ranIdx));
		}
	}
	
	private void swap(Node<E> n1, Node<E> n2) {
		E temp = n2.get();
		n2.set(n1.get());
		n1.set(temp);
	}
	
	@SuppressWarnings("unchecked")
	public E[] toArray(Class<E> clazz) {
		E[] ar = (E[]) Array.newInstance(clazz, size());
		Node<E> node = dummy.next();
		for(int i = 0; i < ar.length; i++) {
			ar[i] = node.get();
			node = node.next();
		}
		return ar;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("[");
		Node<E> cur = dummy.next();
		for(int i = 0; i < size; i++) {
			str.append(cur.get());
			if(i < size-1) str.append(", ");
			cur = cur.next();
		}
		
		str.append("]");
		
		return str.toString();
	}
	
	private static class Node<E> {
		private E data;
		private Node<E> next, prev;
		
		public Node(E data) {
			this(null, data, null);
		}
		
		public Node(Node<E> prev, E data, Node<E> next) {
			this.data = data;
			setNext(next);
			setPrev(prev);
		}
		
		public void setNext(Node<E> next) { this.next = next; }
		public void setPrev(Node<E> prev) { this.prev = prev; }
		
		public Node<E> next() { return next; }
		public Node<E> prev() { return prev; }
		
		public E get() { return data; }
		public void set(E data) { this.data = data; }
		
		public String toString() {
			return data+", next="+(next==null?"none":next.get())+", prev="+(prev==null?"none":prev.get());
		}
	}
}
