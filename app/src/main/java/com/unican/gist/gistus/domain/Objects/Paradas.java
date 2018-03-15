package com.unican.gist.gistus.domain.Objects;

import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by Andres on 15/03/2018.
 */

public class Paradas implements Searchable {
    private String mTitle;
    private Integer id;

    public Paradas(String title, Integer paradaId) {
        mTitle = title;
        this.id = paradaId;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
