package game.util;

// "End" because it orders elements such that the first one returned is the one that comes after all the others, in comparisons.
public class EndHeap<E extends Comparable<E>> {
	
	private E[] elements;
	private int size;
	
	public EndHeap() {
		grow(1000);
		size = 0;
	}
	
	@SuppressWarnings("unchecked")
	private void grow(int size) {
		E[] prev = elements;
		elements = (E[]) new Comparable[size];
		if(prev != null)
			System.arraycopy(prev, 0, elements, 0, size);
	}
	
	public void add(E e) {
		if(size >= elements.length) grow(elements.length+500);
		elements[size] = e;
		size++;
		checkValid(size-1);
	}
	
	public E peek() { if(size() == 0) return null; return elements[0]; }
	public E poll() {
		if(size() == 0) return null;
		
		return remove(0);
	}
	
	public void remove(E e) {
		for(int i = 0; i < size(); i++) {
			if(elements[i].compareTo(e) == 0) {
				remove(i);
				break;
			}
		}
	}
	
	private E remove(int index) {
		E e = elements[index];
		swap(index, size()-1);
		size--;
		checkValid(index);
		return e;
	}
	
	private void checkValid(int index) {
		if(index > 0) {
			// check parent, making sure it is bigger.
			
			int parent = (index-1)/2;
			
			// child ("index") should be less than parent
			if(elements[index].compareTo(elements[parent]) > 0) {
				swap(index, parent);
				checkValid(parent); // "parent" index now contains what was at "index".
				return;
			}
		}
		
		// check children, making sure they are all less
		int leftChildIdx = index*2 + 1;
		if(leftChildIdx >= size()) return;
		
		int rightChildIdx = index*2 + 2;
		
		int highIndex = rightChildIdx >= size() || elements[leftChildIdx].compareTo(elements[rightChildIdx]) > 0 ? leftChildIdx : rightChildIdx; // the index of the larger child
		
		if(elements[index].compareTo(elements[highIndex]) < 0) {
			swap(highIndex, index);
			checkValid(highIndex);
		}
		
		// done!
	}
	
	private void swap(int i1, int i2) {
		E temp = elements[i1];
		elements[i1] = elements[i2];
		elements[i2] = temp;
	}
	
	public int size() { return size; }
	
	@Override
	public String toString() { return toStringByLevel(); }
	
	public String toStringByLevel() {
		StringBuilder str = new StringBuilder();
		
		int power = 0;
		int offset = 0;
		for(int i = 0; i < size(); i++) {
			int pow = (int) Math.pow(2, power);
			if(i >= pow+offset) {
				power++;
				offset += pow;
				str.append(System.lineSeparator());
			}
			str.append(elements[i]).append(" ");
		}
		
		return str.toString();
	}
	
}
