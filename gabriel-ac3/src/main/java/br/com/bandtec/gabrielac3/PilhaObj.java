package br.com.bandtec.gabrielac3;
import java.util.Arrays;
import java.util.Objects;

public class PilhaObj <T> {
    private int topo;
    private T[] pilha;

    public PilhaObj(int capacidade) {
        topo = -1;
        pilha = (T[]) new Object[capacidade];
    }

    public boolean isEmpty() {
        return topo == -1;
    }

    public boolean isFull() {
        return topo == pilha.length - 1;
    }

    public void push(T info) {
        if (!isFull()) {
            pilha[++topo] = info;
        }
        else {
            System.out.println("Pilha cheia");
        }
    }

    public T pop() {
        if (!isEmpty()) {
            return pilha[topo--];
        }
        return null;
    }

    public T peek() {
        if(!isEmpty()) {
            return pilha[topo];
        }
        return null;
    }

    public void exibe() {
        if(isEmpty()) {
            System.out.println("Pilha vazia");
        }
        else {
            for(int i = 0; i <= topo; i++) {
                System.out.println(pilha[i]);
            }
        }
    }

    public PilhaObj<T> multiPop(int number){

        if(number > (topo + 1)){
            return null;
        }else{
            PilhaObj<T> aux = new PilhaObj(number);
            for (int i = 0; i < number; i++){
                aux.push(this.pop());
            }
            return aux;
        }
    }

    public void multiPush(PilhaObj<T> aux){
        while (!aux.isEmpty()){
            this.push(aux.pop());
        }
    }
}
