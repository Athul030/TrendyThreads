package com.athul.library.service;

import com.athul.library.dto.OfferDto;
import com.athul.library.model.Offer;

import java.util.List;

public interface OfferService {

    List<OfferDto> getAllOffers();

    Offer save(OfferDto offerDto);

    OfferDto findById(long id);

    Offer update(OfferDto offerDto);

    void disable(long id);

    void enable(long id);

    void deleteOffer(long id);
}
