package com.scheduling.modules.availability.model;

import com.scheduling.modules.auth.model.User;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "availabilities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  private User provider;

  @Column(nullable = false)
  private Integer dayOfWeek; // 0 = Domingo, 1 = Segunda...

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private boolean active = true;
}
