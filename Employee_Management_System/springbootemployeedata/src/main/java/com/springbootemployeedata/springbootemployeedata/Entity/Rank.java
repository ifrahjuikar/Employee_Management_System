package com.springbootemployeedata.springbootemployeedata.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rank {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankId;

    // Description of the rank
    @Column(nullable = false)
    private String rankDesc;

    // Parent rank of this rank (if any)
    @ManyToOne
    @JoinColumn(name = "parentrankid")
    private Rank parentRank;

}
