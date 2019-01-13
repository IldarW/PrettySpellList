

import net.Ildar.wurm.SpellAction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

class PrettySpellListTests {

    @Test
    void testSpellActionIdUniqueness() {
        List<Short> spellActionIds = Arrays.stream(SpellAction.values()).map(SpellAction::getActionId).collect(Collectors.toList());
        assert spellActionIds.size() == new HashSet<>(spellActionIds).size();
    }
}
