package net.Ildar.wurm;

import com.wurmonline.client.renderer.gui.SpellButton;

public enum SpellAction {
    UnknownSpell(-1, "Unknown spell"),
    Bearpaws(406),
    Bless(245),
    BlessingDark(456, "Blessings of the dark"),
    Bloodthirst(454),
    BreakAltar(258, "Break altar"),
    CharmAnimal(275, "Charm animal"),
    CircleOfCunning(276, "Circle of cunning"),
    Continuum(552),
    Courier(338),
    CureLight(246, "Cure light"),
    CureMedium(247, "Cure medium"),
    CureSerious(248, "Cure serious"),
    DarkMessenger(339, "Dark messenger"),
    DeepTentacles(418, "Deep tentacles"),
    Dirt(453),
    Disease(547),
    Disintegrate(449),
    Dispel(450),
    Dominate(274),
    DrainHealth(255, "Drain health"),
    DrainStamina(254, "Drain stamina"),
    Excel(442),
    Fireheart(424),
    FirePillar(420, "Fire pillar"),
    Fireball(549),
    FlamingAura(277, "Flaming aura"),
    Forecast(560),
    ForestGiant(410, "Forest giant strength"),
    FranticCharge(423, "Frantic charge"),
    Frostbrand(417),
    Fungus(446),
    FungusTrap(433, "Fungus trap"),
    Genesis(408),
    GoatShape(422, "Goat shape"),
    HateAnimal(269, "Animal demise"),
    HateDragons(270, "Dragon demise"),
    HateHumans(267, "Human demise"),
    HateLibila(262, "Libila demise"),
    HateMagranon(260, "Magranon demise"),
    HateFo(259, "Fo demise"),
    HateRegeneration(268, "Troll demise"),
    HateVynora(261, "Vynora demise"),
    Heal(249),
    Hellstrength(427, "Hell strength"),
    HolyCrop(400, "Holy Crop"),
    HumidDrizzle(407, "Humid drizzle"),
    IcePillar(414, "Ice pillar"),
    Incinerate(686),
    KarmaBolt(550, "Karma bolt"),
    KarmaMissile(551, "Karma missile"),
    KarmaSlow(554, "Karma slow"),
    LandOfTheDead(435, "Land of the dead"),
    LifeTransfer(409, "Life transfer"),
    LightOfFo(438, "Light of Fo"),
    LightToken(421, "Light token"),
    Lightning(561),
    LocateArtifact(271, "Locate artifact"),
    LocatePlayer(419, "Locate soul"),
    LurkerDark(459, "Lurker in the dark"),
    LurkerDeep(457, "Lurker in the deep"),
    LurkerWoods(458, "Lurker in the woods"),
    MassStamina(425, "Mass stamina"),
    Mend(251),
    MindStealer(415, "Mind stealer"),
    MirroredSelf(562, "Mirrored self"),
    MoleSenses(439, "Mole senses"),
    MorningFog(282, "Morning fog"),
    Nimbleness(416),
    Nolocate(451),
    Oakshell(404),
    Opulence(280),
    PainRain(432, "Pain rain"),
    Phantasms(426),
    ProtectionFo(263, "Fo's Touch"),
    ProtectionLibila(266, "Libila's shielding"),
    ProtectionMagranon(264, "Magranon's shield"),
    ProtectionVynora(265, "Vynora's Hand"),
    Rebirth(273),
    Refresh(250),
    RevealCreatures(444, "Reveal creatures"),
    RevealSettlements(443, "Reveal settlements"),
    RiteDeath(402, "Rite of Death"),
    RiteSpring(403, "Rite of Spring"),
    RitualSun(401, "Ritual of the Sun"),
    RottingGut(428, "Rotting gut"),
    RottingTouch(281, "Rotting touch"),
    RustMonster(548, "Rust monster"),
    ScornOfLibila(448, "Scorn of Libila"),
    ShardOfIce(485, "Shard of ice"),
    SharedPain(278, "Aura of Shared Pain"),
    SixthSense(376, "Sixth sense"),
    Smite(252),
    SproutTrees(634, "Sprout trees"),
    Stoneskin(553),
    Strongwall(440),
    Summon(559),
    SummonSkeleton(631, "Summon a skeleton"),
    SummonWorg(629, "Summon a worg"),
    SummonWraith(630, "Summon a wraith"),
    Sunder(253),
    Tangleweave(641),
    Tornado(413),
    Truehit(447),
    Truestrike(555),
    Venom(412),
    Vessel(272),
    WallOfFire(557, "Wall of fire"),
    WallOfIce(556, "Wall of ice"),
    WallOfStone(558, "Wall of stone"),
    Ward(437),
    Weakness(429),
    WebArmour(455, "Web armour"),
    WildGrowth(436, "Wild growth"),
    WillowSpine(405, "Willow spine"),
    WindOfAges(279, "Wind of ages"),
    WisdomVynora(445, "Wisdom of Vynora"),
    WormBrains(430, "Worm brains"),
    WrathMagranon(441, "Wrath of Magranon"),
    ZombieInfestation(431, "Zombie infestation");

    short actionId;
    String description;

    SpellAction(int actionId) {
        this.actionId = (short) actionId;
        this.description = this.name();
    }

    SpellAction(int actionId, String description) {
        this.actionId = (short) actionId;
        this.description = description;
    }

    public short getActionId() {
        return actionId;
    }

    public String getDescription() {
        return description;
    }

    public static SpellAction getByActionId(short id) {
        for(SpellAction spellAction : values())
            if (spellAction.getActionId() == id)
                return spellAction;
        return UnknownSpell;
    }
}
