package com.devsancabo.www.publicationsrw;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Populator<T> {
    private final Map<String, Thread> populatorMap = new HashMap<>();
    private final Integer populatorUserRatio;
    private Supplier<T> producer;
    private Consumer<T> create;

    public Populator(){
        this.populatorUserRatio = 10;
    }

    public Populator(Integer populatorUserRatio, Supplier<T> producer, Consumer<T> create){
        this.populatorUserRatio = populatorUserRatio;
        this.producer = producer;
        this.create = create;
    }

    public String populateDB(Integer intensity){
        for(int i=1;i<intensity;++i){
            Thread thread = new Thread(new DBPopulator(intensity));
            thread.setName("Populator-" + i);
            thread.start();
            populatorMap.put(thread.getName(),thread);
        }
        return "Requested populator of size " + intensity + ".";
    }

    public void stopPopulators() {
        populatorMap.forEach((k, v) -> v.interrupt());
        while(!populatorMap.entrySet().stream().allMatch(e -> e.getValue().isInterrupted())) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                populatorMap.clear();
            }
        }
        populatorMap.clear();
    }

    public Map<String, Object> gerPopulator() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("currentPopulators:", populatorMap.size());
        dto.put("activePopulators:", populatorMap.entrySet().stream().filter(e -> !e.getValue().isInterrupted()).count());
        return dto;
    }

    private class DBPopulator implements Runnable{
        private final Integer intensity;
        private boolean error = false;
        public DBPopulator(final Integer intensity){
            this.intensity = intensity;
        }
        @Override
        public void run() {
            for(int i=0; i<intensity && !error; i++){
                for(int j=0; j<populatorUserRatio; j++){
                    T dto = producer.get();
                    error = Objects.isNull(dto);
                    if(error) break;
                    create.accept(dto);
                }
            }
        }
    }
}
