package org.jmc.threading;

import org.jmc.geom.FaceUtils.Face;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadOutputQueue {

	private Queue<ChunkOutput> outputQueue = new LinkedList<ChunkOutput>();
	private boolean finished = false;
	
	public static class ChunkOutput {

		private List<Face> faces;

		public ChunkOutput(List<Face> faces) {
			this.faces = faces;
		}

		public List<Face> getFaces() {
			return faces;
		}
	}
	
	public synchronized void add(ChunkOutput outChunk){
		outputQueue.add(outChunk);
		notify();
	}

	public synchronized ChunkOutput getNext() throws InterruptedException {
		while (true) {
			if (!outputQueue.isEmpty()) {
				return outputQueue.remove();
			} else if (finished) {
				return null;
			}
			wait();
		}
	}
	
	public synchronized void finish(){
		finished = true;
		notifyAll();
	}
}
