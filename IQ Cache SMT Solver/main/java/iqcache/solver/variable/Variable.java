package iqcache.solver.variable;
/**
 * Copyright (c) 2009-13
 * The IQCache Team
 * 
 * In case of any questions or comments please feel free to contact
 * Christoph Ehlers ( cehlers@acm.org ).
 *
 * All Rights Reserved.
 *
 * IQCache is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License (GPL)
 * as published by the Free Software Foundation; 
 * either version 3 of the License, or (at your option) any later version.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, you can find the GPL at "http://www.gnu.org/copyleft/gpl.html".
 */
import iqcache.query.column.Column;

/**
 * An implementation of {@link Variable} is a typed variable within a
 * {@link SMTLibExpression}, is identifiable by an identifier. Some variables may
 * also have a size. A {@link Variable} refers to a {@link Column}.
 * 
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 2919 $
 * @version $LastChangedDate: 2013-04-26 14:12:54 +0200 (Fri, 26 Apr 2013) $
 * 
 */
public interface Variable {
	
	/**
	 * Return the identifier for this {@link Variable}. NB: Within a given
	 * {@link SMTLibExpression} or {@link YicesExpression} the identifier has to
	 * be unique.
	 * 
	 * @return a unique identifier for this {@link Variable}.
	 */
	String getIdentifier();
	
	/**
	 * Return the type of this {@link Variable}. This must return types
	 * which can be used within the specific solver.
	 * 
	 * @return type of this {@link Variable}
	 */
	String getType();
	
	/**
	 * If this {@link Variable} has a size-property (see {@link #hasSize()}
	 * ), this returns the size.
	 * 
	 * @return the size of this {@link Variable}.
	 * @throws IllegalAccessError if this {@link Variable} has no size.
	 */
	int getSize();
	
	/**
	 * If this {@link Variable} has a size-property (see {@link #hasSize()}
	 * ), this method sets the size.
	 * 
	 * @param size the size, {@code size >= 0}
	 * @throws IllegalArgumentException iff {#code size < 0}
	 */
	void setSize(int size);
	
	/**
	 * Determine if this {@link Variable} has a size.
	 * 
	 * @return {@code true} if this {@link Variable} has a size
	 */
	boolean hasSize();
	
	/**
	 * Return the corresponding {@link Column} for this {@link Variable} .
	 * 
	 * @return the {@link Column} this {@link Variable} represents.
	 */
	Column getColumn();
	
}
