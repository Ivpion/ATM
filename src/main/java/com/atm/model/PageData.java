package com.atm.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.media.sound.JARSoundbankReader;
import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageData<T> {
    private final List<T> content;
    private final PageMetadata metadata;


    @JsonCreator
    @Builder
    public PageData(@JsonProperty("content") List<T> content,
                    @JsonProperty("metadata") PageMetadata metadata) {
        this.content = content;
        this.metadata = metadata;
    }


    @Getter
    public static class PageMetadata {
        private final long size;
        private final long totalElements;
        private final long totalPages;
        private final long number;

        @JsonCreator
        @Builder
        public PageMetadata(@JsonProperty("size") long size,
                            @JsonProperty("totalElements") long totalElements,
                            @JsonProperty("totalPages") long totalPages,
                            @JsonProperty("number") long number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }
    }
}
