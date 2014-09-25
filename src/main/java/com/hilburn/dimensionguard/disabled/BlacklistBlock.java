package com.hilburn.dimensionguard.disabled;

public class BlacklistBlock extends DisabledBlock {
	private int[] dimBlacklist;
	public BlacklistBlock(String init) {
		super(init);
		blacklist(init.substring(init.indexOf(',')+1));
	}
	
	private void blacklist(String blacklist){
		String[] dimensions=blacklist.split(",");
		dimBlacklist=new int[dimensions.length];
		for (int i=0;i<dimBlacklist.length;i++){
			try{
				dimBlacklist[i]=Integer.parseInt(dimensions[i]);
			}catch(NumberFormatException nfe) {};
		}
	}
	
	@Override
	public boolean isDisabled(int dim) {
		for (int d:dimBlacklist){
			if (d==dim) return true;
		}
		return false;
	}

}
