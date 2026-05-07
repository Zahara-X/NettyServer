package org.example.enums;

public enum State {
     W((byte) 2), S((byte) 5), A((byte) 7), D((byte) 9);
     private byte state;
     State(byte state) {
         this.state = state;
     }
     public byte getState() {
         return state;
     }
}