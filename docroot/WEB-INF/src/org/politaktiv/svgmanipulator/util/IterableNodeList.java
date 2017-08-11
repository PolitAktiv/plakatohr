package org.politaktiv.svgmanipulator.util;

import java.util.LinkedList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Wrapper class around a NodeList, based on a {@link LinkedList}. This allows to do
 * all the {@link Iterable} and other magic with nodes that regular lists in Java can do.
 * @author but
 *
 */
public class IterableNodeList extends LinkedList<Node> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5798200640637049152L;

	public IterableNodeList(NodeList list) {
		super();
		for (int i=0; i < list.getLength(); i++) {
			this.add(list.item(i));
		}
		
	}
	
	

}
