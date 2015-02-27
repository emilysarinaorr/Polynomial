package poly;

import java.io.*;
import java.util.StringTokenizer;

/**
 * This class implements a term of a polynomial.
 * 
 * @author runb-cs112
 *
 */
class Term {
	/**
	 * Coefficient of term.
	 */
	public float coeff;
	
	/**
	 * Degree of term.
	 */
	public int degree;
	
	/**
	 * Initializes an instance with given coefficient and degree.
	 * 
	 * @param coeff Coefficient
	 * @param degree Degree
	 */
	public Term(float coeff, int degree) {
		this.coeff = coeff;
		this.degree = degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		return other != null &&
		other instanceof Term &&
		coeff == ((Term)other).coeff &&
		degree == ((Term)other).degree;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (degree == 0) {
			return coeff + "";
		} else if (degree == 1) {
			return coeff + "x";
		} else {
			return coeff + "x^" + degree;
		}
	}
}

/**
 * This class implements a linked list node that contains a Term instance.
 * 
 * @author runb-cs112
 *
 */
class Node {
	
	/**
	 * Term instance. 
	 */
	Term term;
	
	/**
	 * Next node in linked list. 
	 */
	Node next;
	
	/**
	 * Initializes this node with a term with given coefficient and degree,
	 * pointing to the given next node.
	 * 
	 * @param coeff Coefficient of term
	 * @param degree Degree of term
	 * @param next Next node
	 */
	public Node(float coeff, int degree, Node next) {
		term = new Term(coeff, degree);
		this.next = next;
	}
}

/**
 * This class implements a polynomial.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Pointer to the front of the linked list that stores the polynomial. 
	 */ 
	Node poly;
	
	/** 
	 * Initializes this polynomial to empty, i.e. there are no terms.
	 *
	 */
	public Polynomial() {
		poly = null;
	}
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param br BufferedReader from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 */
	public Polynomial(BufferedReader br) throws IOException {
		String line;
		StringTokenizer tokenizer;
		float coeff;
		int degree;
		
		poly = null;
		
		while ((line = br.readLine()) != null) {
			tokenizer = new StringTokenizer(line);
			coeff = Float.parseFloat(tokenizer.nextToken());
			degree = Integer.parseInt(tokenizer.nextToken());
			poly = new Node(coeff, degree, poly);
		}
	}
	
	
	/**
	 * Returns the polynomial obtained by adding the given polynomial p
	 * to this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial to be added
	 * @return A new polynomial which is the sum of this polynomial and p.
	 */
	public Polynomial add(Polynomial p) {
		/*
		 * ERROR CHECK
		 */
		if(p.poly == null){
			return this;
		}else if (this.poly == null){
			return p;
		}else{
			Polynomial ret = new Polynomial();
			ret.poly = new Node(0, 0, null);
			Node front = ret.poly;
			Node entered = p.poly;
			Node thisPol = this.poly;
			/*
			 * ADDS
			 */
			while(entered != null || thisPol != null){
				boolean bothExist = (entered != null & thisPol != null);
				boolean bothEqual = false;
				if(bothExist){
					bothEqual = (entered.term.degree == thisPol.term.degree);
				}
				if(bothExist && bothEqual){
					ret.poly.term = new Term(entered.term.coeff
							+ thisPol.term.coeff, thisPol.term.degree);

					thisPol = thisPol.next;
					entered = entered.next;
				}else{
					if(entered != null && ((thisPol == null) || entered.term.degree < thisPol.term.degree)){
						ret.poly.term = entered.term;
						entered = entered.next;
					}else{
						ret.poly.term = thisPol.term;
						thisPol = thisPol.next;
					}
				}
				ret.poly.next = new Node(0, 0, null);
				ret.poly = ret.poly.next;
			}
			/*
			 * REMOVES ZERO ENTRIES
			 */
			Node prev = null;
			Node curr = front;
			while(curr != null){
				if(curr.term.coeff == 0){
					curr = curr.next;
					if(prev == null){
						prev = curr;
					}else{
						prev.next = curr;
					}
				}else{
					prev = curr;
					curr = curr.next;
				}
			}
			ret.poly = front;
			if(ret.poly.term.coeff == 0 && ret.poly.next.term.coeff == 0){
				Polynomial zero = new Polynomial();
				zero.poly = new Node (0, 0, null);
				return zero;
			}
			else
				return ret;
		}
	}
	
	/*
	 * HELPER METHOD
	 */
	private Node addToP(Node ins, Node list){
		Node tmp = new Node(ins.term.coeff, ins.term.degree, null);	
		//EMPTY LIST
		if(list==null){
			return tmp;
		}else{			
			Node curr = list;
			Node prev = null;
			//NON-EMPTY LIST, TRAVERSE.
			while(curr!=null)
				if(tmp.term.degree>curr.term.degree){					
					if(curr.next==null){
						curr.next=tmp;
						return list;
					}
				prev=curr;
				curr=curr.next;
			}else if(tmp.term.degree==curr.term.degree){
				if(curr.term.coeff+tmp.term.coeff==0){
					if(prev==null)
						return curr.next;
					else{
						prev.next=curr.next;
						return list;
					}
				}
				if(prev==null)
					list = new Node(curr.term.coeff+tmp.term.coeff, curr.term.degree, curr.next);
				else{
					prev.next= new Node(curr.term.coeff+tmp.term.coeff, curr.term.degree, curr.next);
				}
				return list;				
			}else if(tmp.term.degree<curr.term.degree){				
				tmp.next=curr;
				prev.next=tmp;
				return list;
			}	
		}		
		return null;	
	}
	
	
	/**
	 * Returns the polynomial obtained by multiplying the given polynomial p
	 * with this polynomial - DOES NOT change this polynomial
	 * 
	 * @param p Polynomial with which this polynomial is to be multiplied
	 * @return A new polynomial which is the product of this polynomial and p.
	 */
	public Polynomial multiply(Polynomial p) {
		Node f1 = this.poly;
		Node f2 = p.poly;
		Node t = null;
		Node prod = null;
		Polynomial product = new Polynomial();
		while(f1!=null){
			while(f2!=null){
				t = new Node(f1.term.coeff*f2.term.coeff, f1.term.degree+f2.term.degree, null);
				f2=f2.next;
				prod = addToP(t, prod);
			}
			f1=f1.next;
			f2=p.poly;
		}
		product.poly=prod;
		return product;
	}
	
	/**
	 * Evaluates this polynomial at the given value of x
	 * 
	 * @param x Value at which this polynomial is to be evaluated
	 * @return Value of this polynomial at x
	 */
	public float evaluate(float x) {
		float ret = 0;
		Node curr = this.poly;
		while(curr != null){
			float currentValue = (float) Math.pow(x, curr.term.degree);
			currentValue *= curr.term.coeff;
			ret += currentValue;
			curr = curr.next;
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String retval;
		
		if (poly == null) {
			return "0";
		} else {
			retval = poly.term.toString();
			for (Node current = poly.next ;
			current != null ;
			current = current.next) {
				retval = current.term.toString() + " + " + retval;
			}
			return retval;
		}
	}
}
