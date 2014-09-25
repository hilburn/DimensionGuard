package com.hilburn.dimensionguard.disabled;


/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class BlacklistBlock extends DisabledBlock {

	public BlacklistBlock(String init) {
		super(init);
	}
	
	@Override
	public boolean isDisabled(int dim) {
		return super.isDisabled(dim);
	}

}
