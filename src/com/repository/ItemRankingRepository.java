package com.repository;

import com.entities.ItemRanking;
import java.util.List;

public interface ItemRankingRepository {

    /** Delete old ranking and insert new ranking list */
    void replaceAll(List<ItemRanking> ranking);

    /** Get all ranking records */
    List<ItemRanking> findAll();
}