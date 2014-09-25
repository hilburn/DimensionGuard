package com.hilburn.dimensionguard.disabled;

public class WhitelistBlock extends DisabledBlock {
	private int[] dimWhitelist;
	public WhitelistBlock(String init) {
		super(init);
		whitelist(init.substring(init.indexOf(',')+1));
	}
	
	private void whitelist(String whitelist){
		String[] dimensions=whitelist.split(",");
		dimWhitelist=new int[dimensions.length];
		for (int i=0;i<dimWhitelist.length;i++){
			try{
				dimWhitelist[i]=Integer.parseInt(dimensions[i]);
			}catch(NumberFormatException nfe) {};
		}
	}
	
	@Override
	public boolean isDisabled(int dim) {
		for (int d:dimWhitelist){
			if (d==dim) return false;
		}
		return true;
	}

}
