package com.ankit.quicklink.repository;

import java.time.LocalDate;

public interface DailyClickProjection {

    LocalDate getDate();
    Long getClicks();
}
