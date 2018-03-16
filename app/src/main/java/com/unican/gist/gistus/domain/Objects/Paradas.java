package com.unican.gist.gistus.domain.Objects;


/**
 * Created by Andres on 15/03/2018.
 */

public class Paradas  {
    private String title;
    private Integer id;

    public Paradas(String title, Integer paradaId) {
        this.title = title;
        this.id = paradaId;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
