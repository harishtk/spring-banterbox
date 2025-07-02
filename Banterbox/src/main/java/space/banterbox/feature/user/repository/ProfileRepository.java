package space.banterbox.feature.user.repository;

import org.springframework.data.repository.CrudRepository;
import space.banterbox.feature.user.model.Profile;

import java.util.UUID;

public interface ProfileRepository extends CrudRepository<Profile, UUID> {
}