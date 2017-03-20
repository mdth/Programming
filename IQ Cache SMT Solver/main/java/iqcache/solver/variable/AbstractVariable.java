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
import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.Solver;

/**
 * An abstract implementation of {@link Variable} suitable for subclassing by
 * most concrete variable types.
 * 
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 2919 $
 * @version $LastChangedDate: 2013-04-26 14:12:54 +0200 (Fri, 26 Apr 2013) $
 * 
 */
abstract class AbstractVariable implements Variable {

	/**
	 * The variable identifier.
	 */
	final private String identifier;

	/**
	 * The corresponding {@link Column}.
	 */
	final private Column column;

	/**
	 * If this {@link Variable} has a size, here is it.
	 */
	private int size;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}, {@code column != null}.
	 * @throws NullPointerException
	 *             iff {@code column == null}
	 */
	public AbstractVariable(Column column) {
		this.column = column;
		if (column == null) {
			this.identifier = null;
		} else {
			this.identifier = Solver.getVariableName(column);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Column getColumn() {
		return column;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		if (!hasSize()) {
			throw new IllegalAccessError();
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(int size) {
		if (!hasSize()) {
			throw new IllegalAccessError();
		}
		Preconditions.checkArgument(size > 0);
		this.size = size;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	/**
	 * Two instances of type {@link AbstractVariable} are equal iff the
	 * identifiers are equal.
	 * 
	 * @see #identifier
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractVariable)) {
			return false;
		}
		AbstractVariable other = (AbstractVariable) obj;
		if (identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!identifier.equals(other.identifier)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (!hasSize()) {
			return String.format("(define %s::%s)", getIdentifier(), getType());
		} else {
			return String.format("(define %s::(%s %d))", getIdentifier(),
					getType(), getSize());
		}

	}

}
