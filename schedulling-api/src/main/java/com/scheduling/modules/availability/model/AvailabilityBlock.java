package com.scheduling.modules.availability.model;

import com.scheduling.modules.auth.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "availability_blocks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityBlock {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "provider_id", nullable = false)
  private User provider;

  @Column(nullable = false)
  private LocalDateTime startDateTime;

  @Column(nullable = false)
  private LocalDateTime endDateTime;

  @Column private String reason;
}
