package com.inesv.util;

public abstract class MyTaskUtil<T> {
        protected TaskListnerUtil<T> listner;
        public abstract void run();
        public void setListner(TaskListnerUtil<T> listner) {
            this.listner = listner;
        }
        public TaskListnerUtil<T> getlistner() {
            return listner;
        }

}
