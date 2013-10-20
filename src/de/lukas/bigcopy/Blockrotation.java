package de.lukas.bigcopy;

import java.util.ArrayList;
import java.util.HashMap;

class DataValue{
		
	int value;
	
	DataValue next;
	DataValue previous;
	
	public DataValue(int value){
		this.value = value;
	}
	
	public void setNext(DataValue next){
		this.next = next;
	}
	
	public void setPrevious(DataValue previous){
		this.previous = previous;
	}
	
	public DataValue next(){
		return this.next;
	}
	
	public DataValue previous(){
		return this.previous;
	}
	
}

class BlockR{
	int id;
	ArrayList<DataValue> values;

	public BlockR(int id){
		this.id = id;
		this.values = new ArrayList<DataValue>();
	}
	
	public void addRotationSet(int x1, int x2, int x3, int x4){
		DataValue a = new DataValue(x1);
		DataValue b = new DataValue(x2);
		DataValue c = new DataValue(x3);
		DataValue d = new DataValue(x4);
		
		a.setPrevious(d);
		a.setNext(b);
		
		b.setPrevious(a);
		b.setNext(c);
		
		c.setPrevious(b);
		c.setNext(d);
		
		d.setPrevious(c);
		d.setNext(a);
		
		this.values.add(a);
		this.values.add(b);
		this.values.add(c);
		this.values.add(d);
		
	}
	
	public DataValue getDataValue(int value){
		for (DataValue x : this.values){
			if (x.value == value)
				return x;
		}
		return null;
	}

}

public class Blockrotation {

	HashMap<Integer,BlockR> blocks;
	
	public Blockrotation(){
		
		this.blocks = new HashMap<Integer,BlockR>();
		
		BlockR b66 = new BlockR(66);
		b66.addRotationSet(1,0,1,0);
		blocks.put(66,b66);

		BlockR b154 = new BlockR(154);
		b154.addRotationSet(5,2,4,3);
		blocks.put(154,b154);

		BlockR b157 = new BlockR(157);
		b157.addRotationSet(1,0,1,0);
		blocks.put(157,b157);

		BlockR b158 = new BlockR(158);
		b158.addRotationSet(4,3,5,2);
		blocks.put(158,b158);

		BlockR b67 = new BlockR(67);
		b67.addRotationSet(4,7,5,6);
		b67.addRotationSet(0,3,1,2);
		blocks.put(67,b67);

		BlockR b61 = new BlockR(61);
		b61.addRotationSet(4,3,5,2);
		blocks.put(61,b61);

		BlockR b131 = new BlockR(131);
		b131.addRotationSet(1,0,3,2);
		blocks.put(131,b131);

		BlockR b130 = new BlockR(130);
		b130.addRotationSet(4,3,5,2);
		blocks.put(130,b130);

		BlockR b64 = new BlockR(64);
		b64.addRotationSet(8,8,8,8);
		b64.addRotationSet(0,3,2,1);
		blocks.put(64,b64);

		BlockR b65 = new BlockR(65);
		b65.addRotationSet(4,3,5,2);
		blocks.put(65,b65);

		BlockR b135 = new BlockR(135);
		b135.addRotationSet(4,7,5,6);
		b135.addRotationSet(0,3,1,2);
		blocks.put(135,b135);

		BlockR b134 = new BlockR(134);
		b134.addRotationSet(4,7,5,6);
		b134.addRotationSet(0,3,1,2);
		blocks.put(134,b134);

		BlockR b68 = new BlockR(68);
		b68.addRotationSet(4,3,5,2);
		blocks.put(68,b68);

		BlockR b69 = new BlockR(69);
		b69.addRotationSet(2,3,1,4);
		b69.addRotationSet(10,11,9,12);
		blocks.put(69,b69);

		BlockR b26 = new BlockR(26);
		b26.addRotationSet(8,11,10,9);
		b26.addRotationSet(0,3,2,1);
		blocks.put(26,b26);

		BlockR b27 = new BlockR(27);
		b27.addRotationSet(1,0,1,0);
		blocks.put(27,b27);

		BlockR b23 = new BlockR(23);
		b23.addRotationSet(4,3,5,2);
		blocks.put(23,b23);

		BlockR b28 = new BlockR(28);
		b28.addRotationSet(1,0,1,0);
		blocks.put(28,b28);

		BlockR b29 = new BlockR(29);
		b29.addRotationSet(4,3,5,2);
		blocks.put(29,b29);

		BlockR b146 = new BlockR(146);
		b146.addRotationSet(4,3,5,2);
		blocks.put(146,b146);

		BlockR b144 = new BlockR(144);
		b144.addRotationSet(4,3,5,2);
		blocks.put(144,b144);

		BlockR b145 = new BlockR(145);
		b145.addRotationSet(6,5,4,7);
		b145.addRotationSet(10,9,8,11);
		b145.addRotationSet(2,1,0,3);
		blocks.put(145,b145);

		BlockR b143 = new BlockR(143);
		b143.addRotationSet(2,3,1,4);
		blocks.put(143,b143);

		BlockR b149 = new BlockR(149);
		b149.addRotationSet(1,0,3,2);
		b149.addRotationSet(5,4,7,6);
		blocks.put(149,b149);

		BlockR b77 = new BlockR(77);
		b77.addRotationSet(2,3,1,4);
		blocks.put(77,b77);

		BlockR b76 = new BlockR(76);
		b76.addRotationSet(2,3,1,4);
		blocks.put(76,b76);

		BlockR b108 = new BlockR(108);
		b108.addRotationSet(0,3,1,2);
		b108.addRotationSet(4,7,5,6);
		blocks.put(108,b108);

		BlockR b109 = new BlockR(109);
		b109.addRotationSet(4,7,5,6);
		b109.addRotationSet(0,3,1,2);
		blocks.put(109,b109);

		BlockR b71 = new BlockR(71);
		b71.addRotationSet(0,3,2,1);
		b71.addRotationSet(8,8,8,8);
		blocks.put(71,b71);

		BlockR b128 = new BlockR(128);
		b128.addRotationSet(0,3,1,2);
		b128.addRotationSet(4,7,5,6);
		blocks.put(128,b128);

		BlockR b93 = new BlockR(93);
		b93.addRotationSet(13,12,15,14);
		b93.addRotationSet(1,0,3,2);
		b93.addRotationSet(5,4,7,6);
		b93.addRotationSet(9,8,11,10);
		blocks.put(93,b93);

		BlockR b107 = new BlockR(107);
		b107.addRotationSet(3,2,1,0);
		blocks.put(107,b107);

		BlockR b96 = new BlockR(96);
		b96.addRotationSet(2,1,3,0);
		blocks.put(96,b96);

		BlockR b114 = new BlockR(114);
		b114.addRotationSet(4,7,5,6);
		b114.addRotationSet(0,3,1,2);
		blocks.put(114,b114);

		BlockR b33 = new BlockR(33);
		b33.addRotationSet(4,3,5,2);
		blocks.put(33,b33);

		BlockR b54 = new BlockR(54);
		b54.addRotationSet(4,3,5,2);
		blocks.put(54,b54);

		BlockR b50 = new BlockR(50);
		b50.addRotationSet(2,3,1,4);
		blocks.put(50,b50);

		BlockR b53 = new BlockR(53);
		b53.addRotationSet(4,7,5,6);
		b53.addRotationSet(0,3,1,2);
		blocks.put(53,b53);

		BlockR b136 = new BlockR(136);
		b136.addRotationSet(4,7,5,6);
		b136.addRotationSet(0,3,1,2);
		blocks.put(136,b136);
	}
	
	public byte getRotatedDataValue(int blockId, byte datavalue, int rotateTimes){
		if (rotateTimes == 0)
			return datavalue;
		BlockR block = this.blocks.get(blockId);
		if (block == null)
			return datavalue;
		DataValue v = block.getDataValue(datavalue);
		if (v == null)
			return datavalue;
		// roatate
		for (int i=0;i<Math.abs(rotateTimes);i++){
			if (rotateTimes < 0){
				v = v.previous();
			} else {
				v = v.next();
			}
		}
		return (byte)v.value;
	}

}
