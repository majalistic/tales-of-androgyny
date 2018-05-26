package com.majalis.encounter;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.screens.BattleScreen.Outcome;
import com.majalis.character.Perk;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.encounter.EncounterBuilder.ChoiceCheckType;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveManager.GameOver;
import com.majalis.scenes.CheckScene.CheckType;
import com.majalis.scenes.ShopScene.ShopCode;
import com.majalis.screens.TownScreen.TownCode;
/*
 * Represents all the metadata for an Encounter and its representation on the world map.
 */
public enum EncounterCode {
	DEFAULT, 
	ERROR, 
	INITIAL, 
	
	WERESLUT, 
	HARPY, 
	SLIME,
	BRIGAND,
	DRYAD, 
	CENTAUR, 
	GOBLIN (ENCHANTED_FOREST),
	GOBLIN_MALE (ENCHANTED_FOREST),
	GADGETEER (FOREST_INACTIVE), 
	ORC (FOREST_INACTIVE),
	ADVENTURER (FOREST_INACTIVE),
	TRUDY_COMPANION, 
	OGRE, 
	BEASTMISTRESS,
	SPIDER (CASTLE),
	ELF,
	ELF_COMPANION, 
	GOLEM,
	GHOST, 
	BUNNY,
	ANGEL (CASTLE),
	ALTAR (CASTLE),
	NAGA,
	MOUTH_FIEND (CASTLE),
	MOUTH_FIEND_ESCAPE,
	MERMAID,
	WARLOCK,
	GIANTESS_FUTA (MOUNTAIN_ACTIVE),
	DULLAHAN,
	
	FORT (CASTLE), 
	TOWN (AssetEnum.TOWN),
	TOWN2 (AssetEnum.TOWN),
	TOWN3 (AssetEnum.TOWN),
	BANK,
	BROTHEL,
	TOWN_CRIER,
	TAVERN, 
	WITCH_COTTAGE (AssetEnum.COTTAGE),
	QUETZAL (MOUNTAIN_ACTIVE), 
	INN,
	INN_MONSTER,
	CARRIAGE,
	CARRIAGE_MONSTER,
	SHOP, 
	SHOP_MONSTER, 
	WEAPON_SHOP,
	STARVATION,
	CAMP_AND_EAT, 
	LEVEL_UP, 
	FORAGE,
	LEAVE_MAP,
	RETURN_MAP,
	
	/* Mini Encounters */
	FOOD_CACHE,
	GOLD_CACHE,
	ICE_CREAM,
	HUNGER_CHARM,
	DAMAGE_TRAP,
	ANAL_TRAP,
	
	/* Story Mode */
	COTTAGE_TRAINER (AssetEnum.COTTAGE),
	TOWN_STORY (AssetEnum.TOWN),
	FIRST_BATTLE_STORY, 
	MERI_COTTAGE (AssetEnum.COTTAGE),
	OGRE_WARNING_STORY (FOREST_INACTIVE),
	OGRE_STORY, 
	HARPY_STORY (MOUNTAIN_ACTIVE),
	BRIGAND_STORY (MOUNTAIN_ACTIVE),
	ORC_STORY,
	WEREWOLF_STORY,
	GHOST_STORY,
	ECCENTRIC_MERCHANT,
	STORY_FEM (FOREST_INACTIVE), 
	STORY_SIGN (FOREST_INACTIVE), 
	END_OF_STORY,
	
	SOLICITATION, 
	CAMP_MASTURBATE, 
	;
	
	private final AssetEnum texture;	
	
	private EncounterCode() {
		this(FOREST_ACTIVE);
	}
	
	private EncounterCode(AssetEnum texture) {
		this.texture = texture;
	}

	public AssetEnum getTexture() { return texture; }
	
	public boolean hasGenericTile() { return getTexture() == AssetEnum.FOREST_ACTIVE || getTexture() == AssetEnum.ENCHANTED_FOREST || getTexture() == AssetEnum.FOREST_INACTIVE; }
	
	public TownCode getTownCode() { return this == TOWN ? TownCode.TOWN : this == TOWN2 ? TownCode.TOWN_STORY : TownCode.TOWN_MONSTER; }
	
	public String getDescription(int visibility) {
		if (this == EncounterCode.DEFAULT) { return "Nothing here."; }
		switch(visibility) {
			case -1: return ""; // special case for display no info
			case 0:
				return "You are unsure of what awaits you!";
			case 1:
				switch (this) {
					case WERESLUT: return "Wereslut";
					case HARPY: return "Harpy";
					case SLIME: return "Slime";
					case BRIGAND: return "Brigand";
					case DRYAD: return "Dryad";
					case CENTAUR: return "Centaur";
					case GOBLIN: return "Enchanted Forest";
					case GOBLIN_MALE: return "Enchanted Forest";
					case ORC: return "Orc";
					case OGRE: return "Ogre";
					case BEASTMISTRESS: return "Drow";
					case ANGEL: return "Ruins";
					case ALTAR: return "Altar";
					case SPIDER: return "Ruins";
					case ADVENTURER: 
					case ELF: return "Adventurer";
					case GOLEM: return "Statue";
					case GADGETEER: return "Merchant";
					case NAGA: return "Cave";
					case MERMAID: return "Island";
					case DULLAHAN: return "Dullahan";
					case TOWN3:
					case TOWN: return "Small Settlement";
					case TOWN2:
					case TOWN_STORY:
						return "Town of Nadir";	
					case FORT: return "Fort";
					case MOUTH_FIEND: return "Strange castle";
					case MOUTH_FIEND_ESCAPE: return "Forest";
					case GIANTESS_FUTA: return "Valley";
					case QUETZAL: return "Mountain";
					case COTTAGE_TRAINER: return "Cottage on the Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing";
					case MERI_COTTAGE: return "Witch's Cottage";
					case WITCH_COTTAGE: return "Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case HARPY_STORY: return "South Pass";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case BRIGAND_STORY: return "West Pass";
					case ORC_STORY: return "Valley";
					case WEREWOLF_STORY: return "Dark Forest";
					case END_OF_STORY: return "Beyond";
					case GHOST:
					case FOOD_CACHE: 
					case GOLD_CACHE:
					case SOLICITATION: 
					case ICE_CREAM:
					case HUNGER_CHARM: 
					case DAMAGE_TRAP: 
					case ANAL_TRAP: return "Unknown";
					case LEAVE_MAP:
					case RETURN_MAP: return "Beyond";
					default: return "Unknown - No Info for encounter #" + this + " and perception level = " + visibility;
				}
			case 2:
				switch (this) {
					case NAGA: return "Cave - Unknown Danger!";
					default:
				}
			case 3:
			case 4:
			case 5:
			case 6:
			default:
				switch (this) {
					case WERESLUT: return "Wereslut - Hostile!";
					case HARPY: return "Harpy - Hostile!";
					case SLIME: return "Slime - Neutral";
					case BRIGAND: return "Brigand - Hostile!";
					case DRYAD: return "Dryad - Peaceful";
					case CENTAUR: return "Centaur - Neutral";
					case GOBLIN: return "Goblin Female - Hostile!";
					case GOBLIN_MALE: return "Goblin Male - Hostile!";
					case ORC: return "Orc - Neutral";
					case ADVENTURER: return "Adventurer - Neutral";
					case ELF: return "Elf - Peaceful";
					case GHOST: return "Strange Presence";
					case GOLEM: return "Golem";
					case OGRE: return "Ogre - Danger!";
					case BEASTMISTRESS: return "Drow Beastmistress - Hostile!";
					case ANGEL: return "Ruins - Calm";
					case ALTAR: return "Altar";
					case SPIDER: return "Ruins - Danger!";
					case GADGETEER: return "Suspicious Merchant";
					case NAGA: return "Cave - Naga Within!";
					case MERMAID: return "Island";
					case DULLAHAN: return "Dullahan";
					case TOWN3: return "Town of Monsters";
					case TOWN: return "Town of Silajam";
					case TOWN2:
					case TOWN_STORY: return "Town of Nadir";
					case FORT: return "Fort";
					case MOUTH_FIEND: return "Strange castle";
					case MOUTH_FIEND_ESCAPE: return "Forest";
					case GIANTESS_FUTA: return "Valley";
					case QUETZAL: return "Mount Xiuh";
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing - signs of hostile creature";
					case MERI_COTTAGE: return "Witch's Cottage";
					case WITCH_COTTAGE: return "Witch's Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case HARPY_STORY: return "South Pass (Harpies)";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case BRIGAND_STORY: return "West Pass (Brigands)";
					case ORC_STORY: return "Valley (Orcs)";
					case WEREWOLF_STORY: return "Dark Forest (Wolves)";
					case END_OF_STORY: return "Beyond";
					case SOLICITATION: return "Strange person";
					case FOOD_CACHE:
					case GOLD_CACHE:
					case ICE_CREAM:
					case HUNGER_CHARM: return "Cache!";
					case DAMAGE_TRAP: 
					case ANAL_TRAP: return "Trap!";
					case LEAVE_MAP:
					case RETURN_MAP: return "Beyond";
					default: return "Unknown - No Info for encounter #" + this  + " and perception level = " + visibility;
				}
		}
	}
	
	public String getFullDescription() {
		switch(this) {
			case TOWN: return "Town of Silajam (visited)";
			case TOWN2: return "Town of Nadir (visited)";
			case TOWN3: return "Town of Monsters (visited)";
			case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts (visited)";
			case WITCH_COTTAGE: return "Witch's Cottage (visited)";
			case QUETZAL: return "Xiuh Mountain (visited)";
			case MERI_COTTAGE: return "Witch's Cottage (visited)";
			case GADGETEER: return "Strange Gadgeteer";
			case FORT: return "Fort";
			case MOUTH_FIEND: return "Strange castle";
			case MOUTH_FIEND_ESCAPE: return "Forest";
			case MERMAID: return "Mermaid";
			case ALTAR: return "Altar";
			case GIANTESS_FUTA: return "Valley of the Giantess";
			case END_OF_STORY: return "Beyond";
			case LEAVE_MAP:
			case RETURN_MAP: return "Beyond";
			default: return "Nothing here.";
		}
	}

	public GameContext getContext() { return this == EncounterCode.TOWN || this == EncounterCode.TOWN2 || this == EncounterCode.TOWN3 ? GameContext.TOWN : GameContext.ENCOUNTER; } 
	
	// for random gen
	public static IntMap<Array<EncounterCode>> encounterMap;
	private static boolean iceCreamReady;
	private static boolean hungerCharmReady;
	static {
		encounterMap = new IntMap<Array<EncounterCode>>();
		encounterMap.put(1, new Array<EncounterCode>(new EncounterCode[]{WERESLUT, HARPY, SLIME, SOLICITATION, BRIGAND, CENTAUR, GOBLIN, GOBLIN_MALE, ORC, FOOD_CACHE, GOLD_CACHE, DAMAGE_TRAP, ANAL_TRAP, HUNGER_CHARM}));
		encounterMap.put(2, new Array<EncounterCode>(new EncounterCode[]{WERESLUT, HARPY, BRIGAND, DRYAD, CENTAUR, GOBLIN, GOBLIN_MALE, ORC, OGRE, HUNGER_CHARM, ICE_CREAM, BEASTMISTRESS, GOLEM, GHOST, GOLD_CACHE, FOOD_CACHE, DAMAGE_TRAP, ANAL_TRAP}));
		encounterMap.put(3, new Array<EncounterCode>(new EncounterCode[]{ORC, OGRE, BEASTMISTRESS, DULLAHAN, GOLEM, GHOST, NAGA, FOOD_CACHE, DAMAGE_TRAP, ANAL_TRAP}));
		iceCreamReady = true;
		hungerCharmReady = true;
	}
	
	// FOR TESTING PURPOSES ONLY
	public static void resetState() { 
		iceCreamReady = true;
		hungerCharmReady = true;
	}
	
	public static ObjectSet<EncounterCode> getAllRandomEncounters() {
		ObjectSet<EncounterCode> temp = new ObjectSet<EncounterCode>();
		for (Array<EncounterCode> mapFlat : encounterMap.values()) {
			temp.addAll(mapFlat);
		}
		return temp;
	}
	
	public static Array<EncounterCode> getDifficultySet(int difficulty) { return encounterMap.get(difficulty); }
	
	public static EncounterCode getEncounterCode(int rawCode, int difficulty, ObjectSet<EncounterCode> unspawnedEncounters) {
		for (int difficultyTier = difficulty; difficultyTier >= 1; difficultyTier--) {
			for (EncounterCode encounter : encounterMap.get(difficultyTier)) {
				if (unspawnedEncounters.contains(encounter)) {
					unspawnedEncounters.remove(encounter);
					return encounter;
				}
			}
		}
		
		Array<EncounterCode> encounterArray = encounterMap.get(difficulty);
		EncounterCode newEncounter = encounterArray.get(rawCode % encounterArray.size);
		if (newEncounter == ICE_CREAM) {
			if (iceCreamReady) {
				iceCreamReady = false;
			}
			else {
				newEncounter = FOOD_CACHE;
			}
		}
		else if (newEncounter == HUNGER_CHARM) {
			if (hungerCharmReady) {
				hungerCharmReady = false;
			}
			else {
				newEncounter = FOOD_CACHE;
			}
		}
		for (EncounterCode encounter : encounterArray) {
			if (unspawnedEncounters.contains(encounter)) {
				newEncounter = encounter;
			}
		}
		unspawnedEncounters.remove(newEncounter);
		return newEncounter;
	}

	public EncounterBounty getMiniEncounter() {
		switch (this) {
			case SOLICITATION:
			case FOOD_CACHE:
			case GOLD_CACHE:
			case ICE_CREAM:
			case HUNGER_CHARM: 
			case DAMAGE_TRAP: 
			case ANAL_TRAP: return new EncounterBounty(this);
			default: return null;
		}
	}
	
	public String getScriptPath() { return "script/encounters.json"; }
	
	protected Branch getEncounter(EncounterBuilder b, GameMode mode) {
		switch (this) {
			case ADVENTURER:
				Branch trudyBattle = b.branch().battleScene(
					BattleCode.ADVENTURER,
					b.branch(Outcome.VICTORY).textScene("ADVENTURER-VICTORY").choiceScene(
						"What's the plan?", 
						b.branch("Mount him").require(ChoiceCheckType.FREE_COCK).textScene("ADVENTURER-TOPPED"), 
						b.branch("MOUNT him").require(ChoiceCheckType.LEWD).textScene("ADVENTURER-BOTTOMED"), 
						b.branch("Rob him").textScene("ADVENTURER-ROBBED")
					),
					b.branch(Outcome.DEFEAT).textScene("ADVENTURER-DEFEAT"),
					b.branch(Outcome.SATISFIED_ANAL).textScene("ADVENTURER-SATISFIED"),
					b.branch(Outcome.SUBMISSION).textScene("ADVENTURER-SUBMISSION")
				);
				Branch trudyCaught = b.branch().textScene("ADVENTURER-TRUDY-CAUGHT");
				Branch playerCaught = b.branch().textScene("ADVENTURER-SNARE-CAUGHT");
				Branch scene1 = b.branch().textScene("ADVENTURER-TRUDY-TRIP").concat(trudyCaught);
				Branch scene2 = b.branch().textScene("ADVENTURER-STEP-OVER").checkScene(
					Stat.AGILITY, 
					b.branch(5).textScene("ADVENTURER-SNARE-DODGE").concat(trudyCaught),
					b.branch(0).textScene("ADVENTURER-SNARE-FAIL").concat(playerCaught)
				);
				return b.branch().textScene("ADVENTURER-INTRO").checkScene(
					CheckType.ADVENTURER_ENCOUNTERED,
					b.branch(true).textScene("ADVENTURER-ENTRANCE"),
					b.branch(false).checkScene(
						CheckType.ADVENTURER_HUNT, 
						b.branch(true).textScene("ADVENTURER-HUNT-INTRO").checkScene(
							Stat.PERCEPTION,
							b.branch(6).textScene("ADVENTURER-SNARE").concat(scene1),
							b.branch(3).textScene("ADVENTURER-SNARE").concat(scene2),
							b.branch(0).concat(playerCaught)
						),
						b.branch(false).checkScene(
							CheckType.TRUDY_GOT_IT,
							b.branch(true).textScene("ADVENTURER-ANGRY-REUNION").concat(trudyBattle),
							b.branch(false).checkScene(
								CheckType.PLAYER_GOT_IT,
								b.branch(true).textScene("ADVENTURER-SMUG-REUNION").concat(trudyBattle),
								b.branch(false).checkScene(
									CheckType.TRUDY_LAST, 
									b.branch(true).textScene("ADVENTURER-END"), 
									b.branch(false).textScene("STICK")
								)
							)
						)
					)
				);
			case ALTAR:
				return b.branch().textScene("ALTAR").choiceScene("Do you return to town?", b.branch("Return to town").textScene("ALTAR-WARP"), b.branch("Leave"));
			case ANGEL:				
				Branch angelOral = b.branch().choiceScene("Fuck her face?", b.branch("Hell Yes!").textScene("ANGEL-DEEPTHROAT"), b.branch("I'm Good").textScene("ANGEL-CUM-TRUMPET"));
				Branch angelConfess = b.branch("Confess your temptations").textScene("ANGEL-BJ").choiceScene(
					"She raises a good point... she DOES have a nice booty...",
					b.branch("Let her be your angel of sodomy?").require(ChoiceCheckType.FREE_COCK).checkScene(Stat.CHARISMA, b.branch(5).textScene("ANGEL-ANAL"), b.branch(0).textScene("ANGEL-ANAL-REJECTION").concat(angelOral)),
					b.branch("Let her blow your trumpet").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-BJ-CONT").concat(angelOral),
					b.branch("Leave")
				);
				
				return b.branch().textScene("ANGEL-INTRO").choiceScene(
					"What do you do?", 
					b.branch("Pray").textScene("ANGEL-PRAY").choiceScene(
						"Are you a devotee?", 
						b.branch("Yes (Lie)").textScene("ANGEL-LIE"), 
						b.branch("No").textScene("ANGEL-TRUTH").choiceScene(
							"What do you do?", 
							angelConfess,
							b.branch("Attack Her").textScene("ANGEL-BATTLE").battleScene(
								BattleCode.ANGEL, 
								b.branch(Outcome.VICTORY).textScene("ANGEL-VICTORY").choiceScene(
									"What do you do?", 
									b.branch("Fuck her in the pussy").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-VAGINAL"),
									b.branch("Fuck her in the ass").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-BUTTSEX"),
									b.branch("Leave her alone")
								), 
								b.branch(Outcome.DEFEAT).textScene("ANGEL-DEFEAT"),  
								b.branch(Outcome.SUBMISSION).textScene("ANGEL-FACESIT"),
								b.branch(Outcome.SATISFIED_ANAL).textScene("ANGEL-PACIFIST").concat(angelConfess) 
							),
							b.branch("Remain Silent").textScene("ANGEL-REJECT")
						)
					), 
					b.branch("Profane the Altar").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-PROFANE"), 
					b.branch("Leave")
				);
			case BANK:
				Branch borrow = b.branch("Borrow 50 GP").textScene("BANK-BORROW");
				Branch payBig = b.branch("Pay Debt - 50 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 50).textScene("BANK-PAY-50"); 
				return b.branch().textScene("BANK").checkScene(
					CheckType.NO_BIG_DEBT,
					b.branch(true).checkScene(	
						CheckType.HAVE_NO_DEBT,
						b.branch(true).choiceScene("Do you want to borrow?", borrow, b.branch("Leave")),
						b.branch(false).choiceScene("Pay or receive loan?", payBig, b.branch("Pay Debt - 10 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("BANK-PAY"), borrow, b.branch("Leave"))
					),
					b.branch(false).textScene("BANK-OVERDRAWN").choiceScene("Do you pay your debts?", payBig, b.branch("Default").textScene("BANK-PAY-HARD").gameEnd())
					
				);
			case BEASTMISTRESS:
				Branch mistressChoice = b.branch("Meow").textScene("BEASTMISTRESS-ACCEPT");		
				Branch impureEnd = b.branch().textScene("BEASTMISTRESS-IMPURE-END");
				Branch kittyImpure = b.branch(1).textScene("BEASTMISTRESS-IMPURE").checkScene(Perk.BEASTMASTER, b.branch(3).checkScene(CheckType.ANY_WILLPOWER, b.branch(true).textScene("BEASTMISTRESS-RESIST2").concat(impureEnd), b.branch(false).textScene("BEASTMISTRESS-QUEEN").gameEnd()), b.branch(0).concat(impureEnd));
				Branch kittySex = b.branch().textScene("BEASTMISTRESS-KITTY2").checkScene(Perk.BEASTMASTER, kittyImpure, b.branch(0).textScene("BEASTMISTRESS-PURE"));
				Branch kittyForeplay = b.branch().textScene("BEASTMISTRESS-KITTY").checkScene(CheckType.FREE_COCK, b.branch(true).textScene("BEASTMISTRESS-NONCAGED").concat(kittySex), b.branch(false).textScene("BEASTMISTRESS-CAGED").concat(kittySex));
				
				return b.branch().textScene("BEASTMISTRESS-INTRO").choiceScene(
					"Snake or Pussy?", 
					b.branch("Snake").textScene("BEASTMISTRESS-ENTRANCE"),
					b.branch("Pussy").textScene("BEASTMISTRESS-PUSSY").battleScene(
						BattleCode.BEASTMISTRESS,
						b.branch(Outcome.VICTORY).textScene("BEASTMISTRESS-VICTORY").choiceScene(
							"Well?", 
							b.branch("Go Spelunking").require(ChoiceCheckType.FREE_COCK).textScene("BEASTMISTRESS-SPELUNKING"), 
							b.branch("Go Home").textScene("BEASTMISTRESS-DECLINE")
						),
						b.branch(Outcome.DEFEAT).textScene("BEASTMISTRESS-DEFEAT").checkScene(
							Perk.BEASTMASTER, 
							b.branch(1).checkScene(
								CheckType.ANY_WILLPOWER, 	
								b.branch(true).choiceScene("Which dick will it be?", b.branch("The elf").textScene("BEASTMISTRESS-RESIST").concat(mistressChoice), b.branch("The kitty").concat(kittyForeplay)), // non-purity branch
								b.branch(false).concat(kittyForeplay) // non-purity branch
							), 
							b.branch(0).choiceScene("Get dicked down?", mistressChoice, b.branch("Door number two").textScene("BEASTMISTRESS-REFUSE").concat(kittyForeplay)) // purity branch
						),
						b.branch(Outcome.SATISFIED_ANAL).checkScene(
							Stat.AGILITY,
							b.branch(4).textScene("BEASTMISTRESS-DODGE"),
							b.branch(0).textScene("BEASTMISTRESS-FAIL")
						)
					)
				);
			case BRIGAND:
				Branch[] battleBranches2 = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("BRIGAND-VICTORY").choiceScene("What do you do?", b.branch("Backstab her").require(ChoiceCheckType.FREE_COCK).textScene("BRIGAND-VICTORY-TOP"), b.branch("Straddle her").textScene("BRIGAND-VICTORY-BOTTOM").require(ChoiceCheckType.LEWD), b.branch("Leave her")), 
					b.branch(Outcome.DEFEAT).textScene("BRIGAND-DEFEAT"),
					b.branch(Outcome.SATISFIED_ANAL).textScene("BRIGAND-SATISFIED-ANAL"),
					b.branch(Outcome.SATISFIED_ORAL).textScene("BRIGAND-SATISFIED-ORAL"),
					b.branch(Outcome.SUBMISSION).textScene("BRIGAND-SUBMISSION"),
				};
				Branch acceptCont = b.branch().textScene("BRIGAND-ACCEPT-CONT").choiceScene(
					"Tell her to pull out?",
					b.branch("Say Nothing").textScene("BRIGAND-CATCH"),
					b.branch("Ask her").textScene("BRIGAND-REQUEST").checkScene(
						Stat.CHARISMA,
						b.branch(4).textScene("BRIGAND-FACIAL"),
						b.branch(0).textScene("BRIGAND-BADTASTE")
					)
				);
				Branch speak = b.branch("Speak").textScene("BRIGAND-HAIL").choiceScene(
					"Accept her offer?",
					b.branch("Accept").require(ChoiceCheckType.LEWD).textScene("BRIGAND-ACCEPT").checkScene(CheckType.PLUGGED, b.branch(true).textScene("BRIGAND-BUTTPLUG").concat(acceptCont), b.branch(false).concat(acceptCont)),
					b.branch("Decline").textScene("BRIGAND-DECLINE").checkScene(
						CheckType.HAS_KYLIRA, 
						b.branch(true).textScene("BRIGAND-KYLIRA"), 
						b.branch(false).checkScene(
							Stat.CHARISMA,
							b.branch(5).textScene("BRIGAND-CONVINCE"),
							b.branch(0).textScene("BRIGAND-FAIL").battleScene(
								BattleCode.BRIGAND, 
								battleBranches2
							)
						)
					)
				);
				Branch brigandSpotted = b.branch(6).textScene("BRIGAND-SPOT").checkScene(
					CheckType.STEALTH_LEVEL_2, 
					b.branch(true).choiceScene(
						"How do you handle the brigand?",
						speak,
						b.branch("Charge").battleScene(
							BattleCode.BRIGAND, Stance.OFFENSIVE, Stance.BALANCED,
							battleBranches2
						),
						b.branch("Ready an Arrow").battleScene(
							BattleCode.BRIGAND, 
							battleBranches2
						).setRange(2).setDelay(1),
						b.branch("Sneak Attack").battleScene(
							BattleCode.BRIGAND, 
							battleBranches2
						).setDelay(2),
						b.branch("Sneak \"Attack\"").require(ChoiceCheckType.FREE_COCK).textScene("BRIGAND-BUTT-SNEAK").choiceScene("Pull Out?", b.branch("Yep").textScene("BRIGAND-PULL-OUT"), b.branch("Nah").textScene("BRIGAND-KEEP-IN"))
					),
					b.branch(false).choiceScene(
						"How do you handle the brigand?",
						speak,
						b.branch("Charge").battleScene(
							BattleCode.BRIGAND, Stance.OFFENSIVE, Stance.BALANCED,
							battleBranches2
						)
					)
				);
				return b.branch().textScene("BRIGAND-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2,
					b.branch(true).concat(brigandSpotted),
					b.branch(false).checkScene(
						Stat.PERCEPTION, 
						brigandSpotted,
						b.branch(4).textScene("BRIGAND-STAB").battleScene(
							BattleCode.BRIGAND, 
							battleBranches2
						),
						b.branch(0).checkScene(
							CheckType.PLUGGED, 
							b.branch(true).textScene("BRIGAND-FOILED-BACKSTAB").battleScene(
								BattleCode.BRIGAND, Stance.FULL_NELSON_BOTTOM, Stance.FULL_NELSON,
								battleBranches2	
							), 
							b.branch(false).textScene("BRIGAND-BACKSTAB").battleScene(
								BattleCode.BRIGAND, Stance.STANDING_BOTTOM, Stance.STANDING,
								battleBranches2	
							)
						)
					)
				);
			case BRIGAND_STORY:
				return b.branch().textScene("STORY-BRIGAND").battleScene(
					BattleCode.BRIGAND_STORY, 
					b.branch(Outcome.VICTORY).textScene("STORY-BRIGAND-VICTORY").choiceScene(
						"What do you want from her?", 
						b.branch("Her companionship").textScene("STORY-BRIGAND-VICTORY-BANG").choiceScene(
							"Cum now?", 
							b.branch("Now").textScene("STORY-BRIGAND-NOW").choiceScene(
								"What do you do?", 
								b.branch("Nothing").textScene("STORY-BRIGAND-BLUEBALLS"), 
								b.branch("Take it").textScene("STORY-BRIGAND-BOTTOM")
							), 
							b.branch("Later").textScene("STORY-BRIGAND-LATER") 
						), 
						b.branch("Her weapon").textScene("STORY-BRIGAND-VICTORY-SWORD")
					), 
					b.branch(Outcome.DEFEAT).textScene("STORY-BRIGAND-DEFEAT").choiceScene("Steel or suck?", b.branch("Steel").textScene("STORY-BRIGAND-DEFEAT-DEATH").gameEnd(), b.branch("Suck").textScene("STORY-BRIGAND-DEFEAT-SUCK")),
					b.branch(Outcome.SATISFIED_ANAL).textScene("BRIGAND-SATISFIED-ANAL")
				);
		
			case BROTHEL:
				Branch faceCrushed = b.branch().textScene("BROTHEL-MADAME-FACECRUSHED").gameEnd();
				Branch feetLick = b.branch().textScene("BROTHEL-MADAME-FEETLICK");
				Branch offerConclusion = b.branch().textScene("BROTHEL-MADAME-OFFER-CONCLUSION");
				Branch offer = b.branch().choiceScene(
					"Do you take her offer?", b.branch("Yes").textScene("BROTHEL-MADAME-OFFER-TAKEN").choiceScene(
						"What else do you want?", 
						b.branch("Your ass").textScene("BROTHEL-MADAME-ASK-ASS").concat(offerConclusion), 
						b.branch("Your golden beauty").textScene("BROTHEL-MADAME-ASK-BEAUTY").concat(offerConclusion), 
						b.branch("More gold").textScene("BROTHEL-MADAME-ASK-GOLD").concat(offerConclusion), 
						b.branch("To be your chair").textScene("BROTHEL-MADAME-ASK-CHAIR").concat(offerConclusion)
					), 
					b.branch("No").textScene("BROTHEL-MADAME-OFFER-REFUSE")
				);
				Branch touchButt = b.branch().textScene("BROTHEL-MADAME-TOUCHBUTT").concat(offer);
				Branch highLust = b.branch().textScene("BROTHEL-MADAME-HIGHLUST").concat(touchButt);
				Branch formBridge = b.branch().textScene("BROTHEL-MADAME-FORMBRIDGE").checkScene(Stat.ENDURANCE, b.branch(5).textScene("BROTHEL-MADAME-HOLDBRIDGE").concat(offer), b.branch(0).textScene("BROTHEL-MADAME-FAILBRIDGE").concat(offer));
				Branch bridgeHighLust = b.branch().textScene("BROTHEL-MADAME-HIGHLUST-BRIDGE").concat(formBridge);
				Branch talkToMadame = b.branch("Ask her about business").checkScene(
					CheckType.MADAME_UNMET, 
					b.branch(true).textScene("BROTHEL-MADAME").choiceScene(
						"What was so funny?", 
						b.branch("Nothing").textScene("BROTHEL-MERCENARY"), 
						b.branch("Her chair creaked").textScene("BROTHEL-MADAME-UNAMUSED").choiceScene(
							"Why is it funny?", 
							b.branch("I'm sorry").textScene("BROTHEL-MADAME-APOLOGIZE").choiceScene("Offer to kiss her pussy?", b.branch("Yes").textScene("BROTHEL-MADAME-COOCHIE").checkScene(Stat.CHARISMA, b.branch(5).textScene("BROTHEL-MADAME-CUNNILINGUS"), b.branch(0).textScene("BROTHEL-MADAME-NOCOOCHIE").concat(feetLick)), b.branch("No").concat(feetLick)), 
							b.branch("I don't know").textScene("BROTHEL-MADAME-IGNORANCE").choiceScene("Is it because she's put on weight?", b.branch("She does have a fat ass").concat(faceCrushed), b.branch("No").textScene("BROTHEL-MADAME-HEELS")), 
							b.branch("Your ass is fat").concat(faceCrushed)
						)
					),
					b.branch(false).checkScene(
						CheckType.MADAME_OFFER_UNGIVEN,
						b.branch(true).textScene("BROTHEL-MADAME-OFFER").checkScene(
							CheckType.HIGH_LUST, 
							b.branch(true).concat(highLust), 
							b.branch(false).checkScene(
								CheckType.ANY_WILLPOWER, 
								b.branch(true).choiceScene(
									"Touch?", 
									b.branch("Touch").concat(touchButt), 
									b.branch("Hold Back (1 WP)").textScene("BROTHEL-MADAME-BRIDGE-OFFER").checkScene(
										CheckType.HIGH_LUST, 
										b.branch(true).concat(bridgeHighLust), 
										b.branch(false).checkScene(
											CheckType.ANY_WILLPOWER, 
											b.branch(true).choiceScene(
												"Be her chair?", 
												b.branch("Form chair").concat(formBridge), 
												b.branch("What? (1 WP)").textScene("BROTHEL-MADAME-REFUSE-FORMBRIDGE").concat(offer)
											),
											b.branch(false).concat(bridgeHighLust)
										)
									)
								), 
								b.branch(false).concat(highLust)
							)
						), 
						b.branch(false).checkScene(
							CheckType.MADAME_OFFER_NOT_ACCEPTED, 
							b.branch(true).concat(offer), 
							b.branch(false).checkScene(
								CheckType.BROTHEL_QUEST_COMPLETE, 
								b.branch(true).textScene("BROTHEL-QUEST-COMPLETE"), 
								b.branch(false).textScene("BROTHEL-MADAME-RETURN")
							)
						)
					)
				);
				
				Branch daisyBJFinish = b.branch("Let her work").textScene("BROTHEL-DAISY-BJ-FINISH");
				Branch roseBJFinish = b.branch("Let her work").textScene("BROTHEL-ROSE-BJ-FINISH");
				Branch brothelMadameCruelOralWarning = b.branch().checkScene(CheckType.CRUEL_ORAL_UNWARNED, b.branch(true).textScene("BROTHEL-MADAME-WARN-CRUEL-ORAL"), b.branch(false).textScene("BROTHEL-MADAME-THREATEN-CRUEL-ORAL"));
				
				Branch daisyForce = b.branch("Grab her").textScene("BROTHEL-DAISY-DEEPTHROAT-FORCE").concat(brothelMadameCruelOralWarning);
				Branch roseForce = b.branch("Grab her").textScene("BROTHEL-ROSE-DEEPTHROAT-FORCE").concat(brothelMadameCruelOralWarning);
				Branch patronBrothel = b.branch("Ask about the girls").checkScene(
					CheckType.CRUEL_ORAL_BANNED, 
					b.branch(true).textScene("BROTHEL-BANNED"), 
					b.branch(false).textScene("BROTHEL-GIRL-DESCRIPTION").choiceScene(
						"Which girl do you want to hire?", 
						b.branch("Daisy").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).require(ChoiceCheckType.FREE_COCK).textScene("BROTHEL-DAISY").choiceScene( // femboy
							"What do you want to do?", 
							b.branch("Get a blowjob").textScene("BROTHEL-DAISY-BJ").checkScene(
								CheckType.HIGH_LUST, 
								b.branch(true).concat(daisyForce), 
								b.branch(false).choiceScene(
								"Ask her to deepthroat?", 
									b.branch("Ask").checkScene(
										Stat.CHARISMA, 
										b.branch(7).textScene("BROTHEL-DAISY-DEEPTHROAT"),
										b.branch(0).textScene("BROTHEL-DAISY-DEEPTHROAT-FAIL").choiceScene(
											"What do you do?", 
											daisyForce,
											daisyBJFinish
										)
									),
									daisyBJFinish
								)
							),
							b.branch("Fuck her").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 5).textScene("BROTHEL-DAISY-BOTTOM")
						),
						b.branch("Rose").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 15).require(ChoiceCheckType.FREE_COCK).textScene("BROTHEL-ROSE").choiceScene( // mystery
							"What do you want to do?", 
							b.branch("Get a blowjob").textScene("BROTHEL-ROSE-BJ").checkScene(
								CheckType.HIGH_LUST, 
								b.branch(true).concat(roseForce), 
								b.branch(false).choiceScene(
									"Ask her to deepthroat?", 
									b.branch("Ask").checkScene(
										Stat.CHARISMA, 
										b.branch(12).textScene("BROTHEL-ROSE-DEEPTHROAT"),
										b.branch(0).textScene("BROTHEL-ROSE-DEEPTHROAT-FAIL").choiceScene(
											"What do you do?", 
											roseForce,
											roseBJFinish
										)
									),
									roseBJFinish
								)
							),
							b.branch("Fuck her").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 5).textScene("BROTHEL-ROSE-BOTTOM")
						),
						b.branch("Ivy").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 20).textScene("BROTHEL-IVY").choiceScene( // futa
							"What do you want to do?", 
							b.branch("Get a blowjob").require(ChoiceCheckType.FREE_COCK).textScene("BROTHEL-IVY-BJ"),
							b.branch("Fuck her").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).require(ChoiceCheckType.FREE_COCK).textScene("BROTHEL-IVY-BOTTOM"),
							b.branch("Get fucked").textScene("BROTHEL-IVY-TOP")
						),
						b.branch("Leave")
					)
				);
				
				Branch onceSignedUp = b.branch().textScene("BROTHEL-MEMBER").choiceScene(
						"What do you ask of her?",
						talkToMadame,
						patronBrothel,
						b.branch("Offer your services as a top").require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.TOP, 3).textScene("BROTHEL-TOP-WARNING").checkScene(Perk.TOP, b.branch(10).textScene("BROTHEL-TOP-MASTER"), b.branch(7).textScene("BROTHEL-TOP-EXPERT"), b.branch(0).textScene("BROTHEL-TOP-NOVICE").checkScene(Stat.ENDURANCE, b.branch(6).textScene("BROTHEL-TOP-NOVICE-SUCCESS"), b.branch(0).textScene("BROTHEL-TOP-NOVICE-FAIL"))),
						b.branch("Offer your services as a bottom").checkScene(
							CheckType.MAX_LUST, 
							b.branch(true).choiceScene("You can't hold back!", b.branch("Pro Bono").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 3).textScene("BROTHEL-EXCITED"), b.branch("Whatever!").textScene("BROTHEL-OPPORTUNIST")), 
							b.branch(false).choiceScene(
								"What service do you offer?", 
								b.branch("Kissing (1 GP)").textScene("BROTHEL-KISSING"), 
								b.branch("Handjobs (2 GP)").textScene("BROTHEL-HANDJOB").checkScene(Perk.CRANK_MASTER, b.branch(3).textScene("BROTHEL-HANDJOB-MASTER"), b.branch(2).textScene("BROTHEL-HANDJOB-EXPERT"), b.branch(1).textScene("BROTHEL-HANDJOB-NOVICE"), b.branch(0).textScene("BROTHEL-HANDJOB-BEGINNER")), 
								b.branch("Blowjobs (3 GP)").textScene("BROTHEL-ORAL").checkScene(Perk.BLOWJOB_EXPERT, b.branch(3).textScene("BROTHEL-ORAL-MASTER"), b.branch(2).textScene("BROTHEL-ORAL-EXPERT"), b.branch(1).textScene("BROTHEL-ORAL-NOVICE"), b.branch(0).textScene("BROTHEL-ORAL-BEGINNER")), 
								b.branch("Ass (5 GP)").choiceScene(
									"With condoms?", 
									b.branch("Yes (5GP)").textScene("BROTHEL-ANAL-CONDOM").checkScene(Perk.PERFECT_BOTTOM, b.branch(3).textScene("BROTHEL-ANAL-CONDOM-MASTER"), b.branch(2).textScene("BROTHEL-ANAL-CONDOM-EXPERT"), b.branch(1).textScene("BROTHEL-ANAL-CONDOM-NOVICE"), b.branch(0).textScene("BROTHEL-ANAL-CONDOM-BEGINNER")),
									b.branch("Bareback (7GP)").require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.PERFECT_BOTTOM, 4).textScene("BROTHEL-ANAL-BAREBACK").checkScene(Perk.PERFECT_BOTTOM, b.branch(6).textScene("BROTHEL-ANAL-BAREBACK-MASTER"), b.branch(0).textScene("BROTHEL-ANAL-BAREBACK-EXPERT"))
								), 
								b.branch("Girlfriend Experience").textScene("BROTHEL-GFXP").require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.PERFECT_BOTTOM, 6),
								b.branch("Never mind"),
							b.branch("Leave")						
						)
					)
				);
				
				return b.branch().textScene("BROTHEL").checkScene(
					CheckType.ELF_BROTHEL,
					b.branch(true).textScene("ELF-BROTHEL"),
					b.branch(false).checkScene(
						CheckType.PROSTITUTE_CLASS_CHANGE,
						b.branch(true).concat(onceSignedUp),
						b.branch(false).checkScene(
							CheckType.PROSTITUTE_WARNING_GIVEN,
							b.branch(true).checkScene(	
								Perk.LADY_OF_THE_NIGHT, 
								b.branch(20).textScene("BROTHEL-CLASS-CHANGE"),
								b.branch(0).concat(onceSignedUp)
							),
							b.branch(false).checkScene(
								Perk.LADY_OF_THE_NIGHT,
								b.branch(10).textScene("BROTHEL-WARNING").concat(onceSignedUp),
								b.branch(0).checkScene(
									CheckType.PROSTITUTE, 	
									b.branch(true).concat(onceSignedUp),
									b.branch(false).choiceScene(
										"What do you ask of her?", 
										talkToMadame,
										patronBrothel,
										b.branch("Ask her about joining").textScene("BROTHEL-OFFER").choiceScene(
											"Do you want to sign up? What's the worst that could happen?",
											b.branch ("Sign Up").require(ChoiceCheckType.LEWD).textScene("BROTHEL-SIGN-UP").concat(onceSignedUp),
											b.branch ("Don't Sign Up")
										),
										b.branch("Leave")
									)
								)	
							)
						)
					)
				);
			case BUNNY:
				String bunnyScene = "BUNNY-SHOW-" + Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
				String bunnyAnalScene = "BUNNY-ANAL-" + Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
				Branch debtEncounter = b.branch().choiceScene(
					"Pay off your debt?", 
					b.branch("Pay 100").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 100).textScene("BUNNY-PAY-100"), 
					b.branch("Pay 50").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 50).textScene("BUNNY-PAY-50"), 
					b.branch ("Pay 10").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("BUNNY-PAY-10"), 
					b.branch("Can't Pay").require(ChoiceCheckType.GOLD_LESS_THAN_X, 10).checkScene(CheckType.DEBT_WARNING, b.branch(true).textScene("BUNNY-PAY-FAIL").battleScene(BattleCode.BUNNY, b.branch(Outcome.VICTORY).textScene("BUNNY-VICTORY"), b.branch(Outcome.DEFEAT).textScene("BUNNY-DEFEAT").textScene(bunnyAnalScene).textScene("BUNNY-DEFEAT-CONT").gameEnd()), b.branch(false).textScene("BUNNY-PAY-WARNING"))
				);
				return b.branch().textScene("BUNNY-INTRO").checkScene(CheckType.DEBT_FIRST_ENCOUNTER, b.branch(true).textScene("BUNNY-FIRST").textScene(bunnyScene).textScene("BUNNY-SHOW").concat(debtEncounter), b.branch(false).textScene(bunnyScene).textScene("BUNNY-SHOW-REUNION").concat(debtEncounter));						
			case CAMP_AND_EAT:
				return b.branch().textScene("FORCED-CAMP");
			case CAMP_MASTURBATE:
				return b.branch().textScene("CAMP-MASTURBATE").checkScene(Perk.ANAL_ADDICT, b.branch(1).textScene("CAMP-MASTURBATE-ANAL"), b.branch(0).textScene("CAMP-MASTURBATE-STROKE"));
			case CARRIAGE:
				return b.branch().textScene("CARRIAGE-INTRO").checkScene(
					CheckType.BEEN_TO_MONSTER_TOWN, 
					b.branch(true).choiceScene(
						"Take a carriage to the monster town?", 
						b.branch("Yes").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("CARRIAGE-TO-MONSTER-TOWN"),
						b.branch("No")
					), 
					b.branch(false).textScene("CARRIAGE-DENIED")
				);
			case CARRIAGE_MONSTER:
				return b.branch().textScene("CARRIAGE-MONSTER-INTRO").checkScene(
						CheckType.BEEN_TO_HUMAN_TOWN, 
						b.branch(true).choiceScene(
							"Take a carriage to the human town?", 
							b.branch("Yes").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("CARRIAGE-TO-HUMAN-TOWN"),
							b.branch("No")
						), 
						b.branch(false).textScene("CARRIAGE-DENIED")
					);
			case CENTAUR:
				Branch powerBottomOptionsUnicorn = b.branch().choiceScene("Suck her off or mount her cock?", b.branch("Get on your knees").textScene("UNICORN-VICTORY-ORAL"), b.branch("Ride 'em cowgirl").textScene("UNICORN-VICTORY-ANAL"));
				Branch powerBottomOptionsCentaur = b.branch().choiceScene("Suck her off or mount her cock?", b.branch("Get on your knees").textScene("CENTAUR-VICTORY-ORAL"), b.branch("Ride 'em cowgirl").textScene("CENTAUR-VICTORY-ANAL"));
				Branch victoryOptionsUnicorn = b.branch().checkScene(
						CheckType.MAX_LUST, 
						b.branch(true).textScene("UNICORN-VICTORY-LUST").concat(powerBottomOptionsUnicorn),
						b.branch(false).choiceScene("What do you do?", b.branch("Get a look at that horse cock").require(ChoiceCheckType.LEWD).textScene("UNICORN-VICTORY-INSPECT").concat(powerBottomOptionsUnicorn), b.branch("Leave"))
					);
				Branch victoryOptionsCentaur = b.branch().checkScene(
					CheckType.MAX_LUST, 
					b.branch(true).textScene("CENTAUR-VICTORY-LUST").concat(powerBottomOptionsCentaur),
					b.branch(false).choiceScene("What do you do?", b.branch("Inspect her big horse asshole").require(ChoiceCheckType.FREE_COCK).textScene("CENTAUR-VICTORY-TOP"), b.branch("Get a look at that horse cock").require(ChoiceCheckType.LEWD).textScene("CENTAUR-VICTORY-INSPECT").concat(powerBottomOptionsCentaur), b.branch("Leave"))
				);
				Branch[] centaurBattle = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("CENTAUR-VICTORY").concat(victoryOptionsCentaur),
					b.branch(Outcome.DEFEAT).textScene("CENTAUR-DEFEAT").checkScene(
						Perk.EQUESTRIAN, 
						b.branch(3).textScene("CENTAUR-GAME-OVER").gameEnd(), 
						b.branch(0).textScene("CENTAUR-DEFEAT-CONT")
					), 
					b.branch(Outcome.SATISFIED_ANAL).textScene("CENTAUR-SATISFIED")
				};
				Branch[] unicornBattle = new Branch[]{b.branch(Outcome.VICTORY).textScene("UNICORN-VICTORY").concat(victoryOptionsUnicorn), b.branch(Outcome.DEFEAT).textScene("UNICORN-DEFEAT")};
				Branch centaurCatamite = b.branch().textScene("CENTAUR-CATAMITE").battleScene(
					BattleCode.CENTAUR, Stance.DOGGY_BOTTOM, Stance.DOGGY,
					centaurBattle
				);
				Branch centaurOptions = b.branch(0).choiceScene(
					"Fight the centaur?",
					b.branch("Fight Her").battleScene(
						BattleCode.CENTAUR,
						centaurBattle
					),
					b.branch("Decline"),
					b.branch("Ask For It").require(ChoiceCheckType.LEWD).checkScene(CheckType.PLUGGED, b.branch(true).textScene("CENTAUR-BUTTPLUG").concat(centaurCatamite), b.branch (false).concat(centaurCatamite))
				);
				
				Branch centaurOrgy = b.branch().textScene("CENTAUR-CAMP-ORGY");
				
				Branch campOptions = b.branch(0).choiceScene(
					"Stay in the camp?",
					b.branch("Stay").concat(centaurOrgy),
					b.branch("Leave")
				);
				
				return b.branch().textScene("CENTAUR-INTRO").checkScene(
					CheckType.STEALTH_LEVEL_3, 
					b.branch(true).textScene("CENTAUR-AVOID"), 
					b.branch(false).checkScene(
						CheckType.VIRGIN, 
						b.branch(true).textScene("UNICORN-ENTRANCE").battleScene(
							BattleCode.UNICORN,
							unicornBattle
						),
						b.branch(false).checkScene(
							CheckType.CENTAUR_FIRST, 
							b.branch(true).textScene("CENTAUR-ENTRANCE").checkScene(
								Perk.ANAL_ADDICT,
								b.branch(3).checkScene(CheckType.ANY_WILLPOWER, b.branch(true).textScene("CENTAUR-WILLPOWER").concat(centaurOptions), b.branch(false).checkScene(CheckType.PLUGGED, b.branch(true).textScene("CENTAUR-BUTTPLUG").concat(centaurCatamite), b.branch(false).concat(centaurCatamite))),
								centaurOptions
							),
							b.branch(false).textScene("CENTAUR-RETURN").choiceScene(
								"Visit the centaur camp?", 
								b.branch("Yes").textScene("CENTAUR-CAMP").checkScene(
									Perk.ANAL_ADDICT,
									b.branch(3).checkScene(
										CheckType.ANY_WILLPOWER, 
										b.branch(true).textScene("CENTAUR-WILLPOWER").concat(campOptions), 
										b.branch(false).concat(centaurOrgy)
									),
									campOptions
								),
								b.branch("No").checkScene(
									Perk.ANAL_ADDICT,
									b.branch(3).checkScene(CheckType.ANY_WILLPOWER, b.branch(true).textScene("CENTAUR-WILLPOWER").concat(centaurOptions), b.branch(false).checkScene(CheckType.PLUGGED, b.branch(true).textScene("CENTAUR-BUTTPLUG").concat(centaurCatamite), b.branch(false).concat(centaurCatamite))),
									centaurOptions
								)
							)
						)
					));
			case COTTAGE_TRAINER:
				return b.branch().checkScene(CheckType.TRAINER_VISITED, b.branch(true).checkScene(CheckType.HAS_ICE_CREAM, b.branch(true).textScene("TRAINER-BLITZ"), b.branch(false).textScene("STORY-004")), b.branch(false).textScene("STORY-003").characterCreation(true)); 		
			case QUETZAL:
				Branch quetzalSeconds = b.branch().textScene("QUETZAL-SECONDS").gameEnd();
				Branch quetzalLoss = b.branch().textScene("QUETZAL-LOSS").concat(quetzalSeconds);
				Branch quetzalAttack = b.branch().checkScene(
					CheckType.BLESSING_PURCHASED, 
					b.branch(true).textScene("QUETZAL-ATTACK").battleScene(BattleCode.QUETZAL, b.branch(Outcome.VICTORY).textScene("QUETZAL-VICTORY").checkScene(CheckType.VIRGIN, b.branch(true).textScene("QUETZAL-VICTORY-VIRGIN"), b.branch(false)), b.branch(Outcome.DEFEAT).concat(quetzalLoss), b.branch(Outcome.DEATH).concat(quetzalLoss), b.branch(Outcome.KNOT_ANAL).concat(quetzalSeconds)), 
					b.branch(false).textScene("QUETZAL-AUTO").concat(quetzalLoss)
				);
				Branch quetzalFirst = b.branch().textScene("QUETZAL-INTRO").choiceScene("Attack?", b.branch("Attack").concat(quetzalAttack), b.branch("Strategic Retreat").textScene("QUETZAL-RETREAT"));
				return b.branch().checkScene(
						CheckType.QUETZAL_SLAIN, 
						b.branch(true).textScene("QUETZAL-SLAIN"), 
						b.branch(false).checkScene(
							CheckType.QUETZAL_MET, 
							b.branch(false).checkScene(
								CheckType.QUETZAL_HEARD, 
								b.branch(true).textScene("QUETZAL-CRIER").concat(quetzalFirst), 
								b.branch(false).textScene("QUETZAL-NO-CRIER").concat(quetzalFirst)
							),
							b.branch(true).textScene("QUETZAL-RETURN").concat(quetzalAttack)
						)
				);
			case DEFAULT:
				return b.branch().textScene("STICK");
			case DULLAHAN:
				Branch flee = b.branch("Flee").textScene("DULLAHAN-RETREAT");
				Branch fight = b.branch("Fight").battleScene(
					BattleCode.DULLAHAN,
					b.branch(Outcome.VICTORY).textScene("DULLAHAN-VICTORY"),  
					b.branch(Outcome.DEFEAT).textScene("DULLAHAN-DEFEAT").choiceScene("Fuck or flee?", b.branch("Fuck").textScene("DULLAHAN-DEFEAT-ANAL"), flee) 
				);
				Branch dullahanGirlfriend = b.branch().choiceScene("Be her girlfriend?", b.branch("I'm not a girl!").textScene("DULLAHAN-REJECTION"), b.branch("Okay").require(ChoiceCheckType.FREE_COCK).textScene("DULLAHAN-SECONDS"));
				Branch dullahanAnalCont2 = b.branch().textScene("DULLAHAN-ANAL-CONT2").concat(dullahanGirlfriend);
				Branch dullahanAnalCont = b.branch().textScene("DULLAHAN-ANAL-CONT").checkScene(
					Perk.ANAL_ADDICT, 
					b.branch(3).textScene("DULLAHAN-ANAL-EJACULATION").concat(dullahanAnalCont2),
					b.branch(0).textScene("DULLAHAN-ANAL-CHOKE").choiceScene("Cum or not?", b.branch("I don't want to cum").textScene("DULLAHAN-ANAL-CHOKE-STOP").choiceScene("Ask her to make you cum?", b.branch("Make me cum!").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("DULLAHAN-REACHAROUND").concat(dullahanAnalCont2), b.branch("Accept it").concat(dullahanAnalCont2)), b.branch("Cum!").textScene("DULLAHAN-ANAL-CHOKE-CUM").concat(dullahanGirlfriend))
				);
				Branch faust = b.branch("Faust").textScene("DULLAHAN-SOUL");				
				Branch fuck = b.branch("Fuck").textScene("DULLAHAN-ACCEPT").choiceScene(
					"What's your move?", 
					b.branch("Give it to her").require(ChoiceCheckType.FREE_COCK).require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.TOP, 2).textScene("DULLAHAN-TOP"),
					b.branch("Take it").require(ChoiceCheckType.LEWD).textScene("DULLAHAN-BOTTOM").choiceScene(
						"What do you do?", 
						b.branch("Stay the course").textScene("DULLAHAN-ANAL").checkScene(
							CheckType.TIGHT_BUTTHOLE, 
							b.branch(true).textScene("DULLAHAN-ANAL-TIGHT").concat(dullahanAnalCont), 
							b.branch(false).textScene("DULLAHAN-ANAL-LOOSE").concat(dullahanAnalCont)
						), 
						b.branch("Give her head").textScene("DULLAHAN-ORAL").checkScene(Perk.BLOWJOB_EXPERT, b.branch(3).textScene("DULLAHAN-ORAL-GOOD"), b.branch(0).textScene("DULLAHAN-ORAL-BAD").choiceScene("Have her demonstrate?", b.branch("Yes").textScene("DULLAHAN-ORAL-SHOWOFF"), b.branch("No").textScene("DULLAHAN-ORAL-FINISH")))
					),
					b.branch("Change your mind").textScene("DULLAHAN-SAD").choiceScene("What do you do?", fight, faust, flee)					
				);
				
				Branch returnHead = b.branch("Return head").textScene("DULLAHAN-RETURNHEAD").choiceScene("Accept?", b.branch("Accept").concat(fuck), b.branch("Leave"));
				Branch caughtByBody = b.branch().textScene("DULLAHAN-CAUGHT").choiceScene("Return the head?", b.branch("Toss it into the water").textScene("DULLAHAN-TOSSHEAD"), returnHead);
				Branch caughtCompanion = b.branch("DULLAHAN-COMPANION").choiceScene("Confront her?", b.branch("Yes").textScene("DULLAHAN-CONFRONT").concat(caughtByBody), b.branch("No").textScene("DULLAHAN-COMPANION-LEWD"));
				return b.branch().checkScene(
					CheckType.STEALTH_LEVEL_3,
					b.branch(true).textScene("DULLAHAN-STEALTH").choiceScene(
						"What do you do?", 
						b.branch("Steal her head").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 5).textScene("DULLAHAN-HEADSTEAL").choiceScene(
							"What do you do?", 
							b.branch("Play keepaway").textScene("DULLAHAN-CHASE").checkScene(
								CheckType.HAS_TRUDY, b.branch(true).concat(caughtCompanion), b.branch(false).checkScene(CheckType.HAS_KYLIRA, b.branch(true).concat(caughtCompanion), b.branch(false).textScene("DULLAHAN-CATCH-UP").concat(caughtByBody))
							), 
							returnHead
						), 
						b.branch("Watch").textScene("DULLAHAN-SELFBJ"), 
						b.branch("Sneak past")
					), 
					b.branch(false).textScene("DULLAHAN-INTRO").choiceScene(
						"What do you do?", 
						fight, 
						fuck,
						faust, 
						flee
					)
				);
			case DRYAD:
				return b.branch().textScene("DRYAD-INTRO").choiceScene(
					"Do you offer her YOUR apple, or try to convince her to just hand it over?",
					b.branch("Offer").require(ChoiceCheckType.LEWD).textScene("DRYAD-OFFER"),
					b.branch("Plead with her").checkScene(
						Stat.CHARISMA,
						b.branch(5).textScene("DRYAD-CONVINCE"),
						b.branch(0).textScene("DRYAD-FAIL")
					)
			    );
			case ECCENTRIC_MERCHANT:
				return b.branch().textScene("STORY-MERCHANT");
			case ELF:
				Branch careerOptions = b.branch().textScene("ELF-CAREER").choiceScene(
					"What do you say?", 
					b.branch("Try the brothel.").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 7).textScene("ELF-BROTHEL-SUGGEST"),
					b.branch("Join me.").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("ELF-JOIN-SUGGEST"),
					b.branch("Become a healer.").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 3).textScene("ELF-HEALER-SUGGEST"),
					b.branch("Go home.").textScene("ELF-LEAVE-SUGGEST")
				);
				return b.branch().textScene("ELF-INTRO").checkScene(
					CheckType.ELF_UNSEEN, 
					b.branch(true).textScene("ELF-ENTER").choiceScene(
						"Do you accept the breakfast invitation?", 
						b.branch("Accept").textScene("ELF-ACCEPT"), 
						b.branch("Decline").textScene("ELF-DECLINE")
					),
					b.branch(false).checkScene(
						CheckType.ELF_DECLINED, 
						b.branch(true).textScene("ELF-RETRY").choiceScene(
							"Do you accept the breakfast invitation?", 
							b.branch("Accept").textScene("ELF-ACCEPT"), 
							b.branch("Decline").textScene("ELF-DECLINE")
						),
						b.branch(false).checkScene(
							CheckType.ELF_ACCEPTED, 
							b.branch(true).textScene("ELF-REUNION").choiceScene(
								"Kiss Kylira?",
								b.branch("Kiss").require(ChoiceCheckType.FREE_COCK).textScene("ELF-TOP").concat(careerOptions),
								b.branch("Be Kissed").textScene("ELF-BOTTOM").concat(careerOptions),
								b.branch("Deny").textScene("ELF-DENY").concat(careerOptions)
							), 
							b.branch(false).checkScene(
								CheckType.ELF_HEALER,
								b.branch(true).textScene("ELF-HEALER"),
								b.branch(false).textScene("STICK")
							)
						)
					)
				);
			case ELF_COMPANION:
				Branch learnHealing = b.branch().choiceScene("Learn healing magic?", b.branch("Learn").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 2).textScene("ELF-COMPANION-LEARN"), b.branch("Not now"));
				return b.branch().checkScene(CheckType.ELF_COMPANION1, 
					b.branch(true).textScene("ELF-COMPANION-FIRST").choiceScene("Spend time with Kylira?", b.branch("Spend time").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 8).textScene("ELF-COMPANION-HANGOUT"), b.branch("Not now")), 
					b.branch(false).checkScene(
						CheckType.ELF_COMPANION2, 
						b.branch(true).textScene("ELF-COMPANION-SECOND").choiceScene(
							"Spend time with Kylira?", 
							b.branch("Spend time").textScene("ELF-COMPANION-CAMPFIRE").concat(learnHealing), 
							b.branch("Not now")
						),
						b.branch(false).checkScene(CheckType.ELF_COMPANION3, b.branch(true).textScene("ELF-COMPANION-SECOND").concat(learnHealing), b.branch(false).textScene("ELF-COMPANION-REPEAT"))
					)
				);
			case END_OF_STORY:
				return b.branch().textScene("STORY-END");
			case ERROR:
				break;
			case FIRST_BATTLE_STORY:
				return b.branch().textScene("STORY-FIGHT-FIRST").battleScene(
					BattleCode.GOBLIN_STORY,
					b.branch(Outcome.VICTORY).textScene("STORY-FIGHT-GOBLIN-VICTORY"),
					b.branch(Outcome.DEFEAT).textScene("STORY-FIGHT-GOBLIN-DEFEAT").gameEnd()
				);
			case FORAGE: 			
				return b.branch().textScene("FORAGE-INTRO").checkScene(
					CheckType.DAY,
					b.branch(true).checkScene(
						// can use this lucky check to divide up into a binary mask for encounter structure (battle, perception check, charisma check), then split the ends up into random text / random battles
						CheckType.LUCKY, 
						b.branch(true).checkScene(
							CheckType.LUCKY, 
							b.branch(true).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-0"), // 3
								b.branch(false).textScene("FORAGE-1") // 1
							),
							b.branch(false).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-2"), // 1
								b.branch(false).textScene("FORAGE-4") // -1
							)
						),
						b.branch(false).checkScene(
							CheckType.LUCKY, 
							b.branch(true).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-3"), // 1
								b.branch(false).textScene("FORAGE-5") // -1
							),
							b.branch(false).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-6"), // -1
								b.branch(false).textScene("FORAGE-7") // -3
							)
						)
					),
					b.branch(false).textScene("FORAGE-NIGHT").checkScene(
						CheckType.LUCKY, 
						b.branch(true).checkScene(
							CheckType.LUCKY, 
							b.branch(true).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-NIGHT-GOOD").textScene("FORAGE-1"), // 3
								b.branch(false).textScene("FORAGE-4") // 1
							),
							b.branch(false).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-4"), // 1
								b.branch(false).textScene("FORAGE-5") // -1
							)
						),
						b.branch(false).checkScene(
							CheckType.LUCKY, 
							b.branch(true).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-4"), // 1
								b.branch(false).textScene("FORAGE-6") // -1
							),
							b.branch(false).checkScene(
								CheckType.LUCKY, 
								b.branch(true).textScene("FORAGE-6"), // -1
								b.branch(false).textScene("FORAGE-7") // -3
							)
						)
					)
				);
			case FORT:
				break;
			case GADGETEER:
				Branch no = b.branch("No thanks").textScene("GADGETEER-NO");
				Branch yes = b.branch().textScene("GADGETEER-SLAVE").gameEnd();
				Branch[] yesyesyes = new Branch[]{b.branch("yes").concat(yes), b.branch("yeS").concat(yes), b.branch("YES").concat(yes)};
				Branch analLoverCheck = b.branch().checkScene(
					Perk.ANAL_ADDICT,
					b.branch(3).textScene("GADGETEER-PEGGED").choiceScene("Become hers?", yesyesyes),
					b.branch(2).textScene("GADGETEER-PLUGS"),
					b.branch(1).textScene("GADGETEER-HESITANT").choiceScene(
						"Try the toys?", 
						b.branch("Yes").textScene("GADGETEER-BALLS"), 
						no
					),
					b.branch(0).textScene("GADGETEER-CONFUSED").choiceScene(
						"Try the toys?", 
						b.branch("Yes").require(ChoiceCheckType.LEWD).textScene("GADGETEER-BREAKINGIN"),
						no
					)
				);
				
				Branch shop = b.branch().choiceScene(
					"Do you want to peruse her wares?", 
					b.branch("Peruse").shopScene(ShopCode.GADGETEER_SHOP).checkScene(
						CheckType.CHASTITIED, 
						b.branch(true).textScene("GADGETEER-CHASTITIED").checkScene(
							CheckType.PALADIN, 
							b.branch(true).textScene("GADGETEER-PALADIN").checkScene(
								Perk.ANAL_ADDICT, 
								b.branch(3).textScene("GADGETEER-SLUT-PALADIN").gameEnd(),
								b.branch(0).textScene("GADGETEER-PURE-PALADIN")
							), 
							b.branch(false).checkScene(CheckType.GADGETEER_TESTED, b.branch(true).checkScene(Perk.ANAL_ADDICT, b.branch(3).textScene("GADGETEER-HONEST").gameEnd(), b.branch(0).textScene("GADGETEER-LIE")), b.branch(false).textScene("GADGETEER-CAGE"))
						), 
						b.branch(false).checkScene(
							CheckType.PLUGGED, 
							b.branch(true).textScene("GADGETEER-PLUGGED").checkScene(
								Perk.ANAL_ADDICT,
								b.branch(3).textScene("GADGETEER-PEGGED").choiceScene("Become hers?", yesyesyes),
								b.branch(0).textScene("GADGETEER-PLUGS")
							),
							b.branch(false).checkScene(
								CheckType.GADGETEER_MET, 
								b.branch(true).checkScene(
									CheckType.GADGETEER_TESTED, 
									b.branch(true).textScene("GADGETEER-GIVECAGE"), 
									b.branch(false).textScene("GADGETEER-TEASE").concat(analLoverCheck)),
								b.branch(false).textScene("GADGETEER-POSTSHOP").concat(analLoverCheck)
							)
						)
					),
					no
				);
				return b.branch().textScene("GADGETEER-INTRO").checkScene(CheckType.GADGETEER_MET, b.branch(true).textScene("GADGETEER-REUNION").concat(shop), b.branch(false).textScene("GADGETEER-FIRST-VISIT").concat(shop));
			case GHOST:
				String spookyGhostScene = Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("blood", true) ? "GHOST-BLOODY" : "GHOST-BLOODLESS";
				Branch ghostPossession = b.branch().textScene("GHOST-POSSESSION").gameEnd();
				Branch ghostBattle = b.branch().battleScene(BattleCode.GHOST, b.branch(Outcome.VICTORY).textScene("GHOST-VICTORY"), b.branch(Outcome.DEFEAT).concat(ghostPossession));
				Branch refuse = b.branch("Refuse").checkScene(Stat.MAGIC, b.branch(2).concat(ghostBattle), b.branch(0).concat(ghostPossession));
				Branch didEnjoy = b.branch().choiceScene("\"Did you enjoy it as much as I did?\", she asks.", b.branch("Yes").textScene("GHOST-DAY-HAPPY"), b.branch("No").textScene("GHOST-DAY-SAD").concat(ghostBattle));
				
				return b.branch().checkScene(
					CheckType.DAY, 
					b.branch(true).textScene("GHOST-DAY").choiceScene(
						"Do you follow?", 
						b.branch("Follow her").textScene("GHOST-DAY-FOLLOW").choiceScene(
							"Receive her \"affection\"?",
							b.branch("Receive it").textScene("GHOST-DAY-RECEIVE").concat(didEnjoy),
							b.branch("Give her your love").textScene("GHOST-DAY-GIVE"),
							b.branch("Reject her").textScene("GHOST-DAY-REJECT").concat(ghostBattle)
						), 
						b.branch("Ignore her")
					), 
					b.branch(false).textScene("GHOST-NIGHT").choiceScene(
						"Do you follow?", 
						b.branch("Follow her").textScene("GHOST-NIGHT-FOLLOW").textScene(spookyGhostScene).textScene("GHOST-NIGHT-CONT").choiceScene(
							"What do you do?", 
							b.branch("Apologize").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GHOST-NIGHT-APOLOGY").choiceScene(
								"Show her love?", 
								b.branch("Show her love").textScene("GHOST-BLOWJOB-WOO-WOO").choiceScene(
									"Well?", 
									b.branch("Point your ass at her").textScene("GHOST-BLASTING").concat(didEnjoy),
									refuse
								),
								refuse
							),
							refuse							
						), 
						b.branch("Ignore her").textScene("GHOST-NIGHT-IGNORE").choiceScene("Camp or Leave?", b.branch("Camp").textScene("GHOST-NIGHT-CAMP").textScene(spookyGhostScene).textScene("GHOST-NIGHT-CAMP-CONT"), b.branch("Leave").textScene("GHOST-NIGHT-LOST"))
					)
				);	
			case GHOST_STORY:
				String spookyGhostScene2 = Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("blood", true) ? "GHOST-BLOODY" : "GHOST-BLOODLESS";
				Branch ghostPossession2 = b.branch().textScene("GHOST-POSSESSION").gameEnd();
				Branch ghostBattle2 = b.branch().battleScene(BattleCode.GHOST, b.branch(Outcome.VICTORY).textScene("GHOST-VICTORY"), b.branch(Outcome.DEFEAT).concat(ghostPossession2));
				return b.branch().textScene("STORY-GHOST").textScene(spookyGhostScene2).textScene("STORY-GHOST-CONT").concat(ghostBattle2);
			case GIANTESS_FUTA:
				Branch rebirth = b.branch().textScene("GIANTESS-REBIRTH");
				Branch selfSacrifice = b.branch().textScene("GIANTESS-SELF-SACRIFICE").concat(rebirth);
				Branch sacrifice = b.branch().checkScene(CheckType.GIANTESS_UNSEEN, b.branch(true).textScene("GIANTESS-VILLAGE-CAPTURE-UNSEEN-GODDESS").concat(selfSacrifice), b.branch(false).textScene("GIANTESS-VILLAGE-CAPTURE-SEEN-GODDESS").concat(selfSacrifice));
				Branch village = b.branch().textScene("GIANTESS-VILLAGE-CAPTURE").concat(sacrifice);
				Branch insideLoincloth = b.branch().textScene("GIANTESS-INSIDE-LOINCLOTH").concat(rebirth);
				Branch exploreValley = b.branch("Explore the valley").textScene("GIANTESS-VALLEY-EXPLORE").checkScene(
						Stat.PERCEPTION, 
						b.branch(5).textScene("GIANTESS-AWARE").choiceScene("Visit the village?", b.branch("Visit").textScene("GIANTESS-VILLAGE").choiceScene("Get baptised?", b.branch("Why not").textScene("GIANTESS-BAPTISM"), b.branch("Nope").textScene("GIANTESS-SACRIFICED").concat(sacrifice)), b.branch("Leave")), 
						b.branch(0).textScene("GIANTESS-UNAWARE").choiceScene(
							"What do you do?", 
							b.branch("Stand and fight").textScene("GIANTESS-FIGHT").battleScene(BattleCode.GIANTESS, 1, b.branch(Outcome.VICTORY), b.branch(Outcome.DEFEAT).concat(rebirth)),
							b.branch("Speak").textScene("GIANTESS-SPEAK").checkScene(Stat.AGILITY, b.branch(5).textScene("GIANTESS-STRUGGLE-SWALLOW").concat(rebirth), b.branch(0).textScene("GIANTESS-INSTANT-SWALLOW").concat(rebirth)),
							b.branch("Run and hide").textScene("GIANTESS-HIDE"), 
							b.branch("Climb her").textScene("GIANTESS-CLIMB").checkScene(
								Stat.AGILITY, 
								b.branch(6).textScene("GIANTESS-FREE-ROAM").choiceScene(
									"Where do you climb?",
									b.branch("Her face").textScene("GIANTESS-FACE").concat(rebirth),
									b.branch("Her hand").textScene("GIANTESS-HAND"),
									b.branch("Her ass").textScene("GIANTESS-ASS"),
									b.branch("Her dong").textScene("GIANTESS-DONG").concat(insideLoincloth)
								), 
								b.branch(0).textScene("GIANTESS-KISS-GAME").checkScene(
									Stat.AGILITY, 
									b.branch(4).textScene("GIANTESS-INSERT-COCK-FLIP"), 
									b.branch(0).textScene("GIANTESS-INSERT-COCK").concat(rebirth)
								)
							)
						)
					);
				
				return b.branch().textScene("GIANTESS-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2, 
					b.branch(true).textScene("GIANTESS-SPOTTED").choiceScene(
						"Approach the goddess?", 
						b.branch("Approach her").textScene("GIANTESS-EXAMINE").choiceScene(
							"What do you do?", 
							b.branch("Observe from distance").textScene("GIANTESS-WET-DREAM").concat(exploreValley), 
							b.branch("Get in close").textScene("GIANTESS-CLOSE-EXAMINE").choiceScene(
								"Do you hide in her loincloth for warmth?", 
								b.branch("Yeah sure why not").concat(insideLoincloth),
								b.branch("What?").textScene("GIANTESS-WET-DREAM-SPLATTERED").concat(village)
							)
						),
						exploreValley
					), 
					b.branch(false).textScene("GIANTESS-BLUNDERED").concat(village)
				 );
			case GOBLIN_MALE:
				Branch maleDefeatCont = b.branch().textScene("GOBLIN-MALE-DEFEAT");
				return b.branch().textScene("GOBLIN-MALE-INTRO").battleScene(
					BattleCode.GOBLIN_MALE,
					b.branch(Outcome.VICTORY).textScene("GOBLIN-MALE-VICTORY").choiceScene("Ride ze gobbo?", b.branch("Gobbo's waiting!").textScene("GOBLIN-MALE-VICTORY-BOTTOM").require(ChoiceCheckType.LEWD), b.branch("No")),
					b.branch(Outcome.DEFEAT).checkScene(CheckType.PLUGGED, b.branch(true).textScene("GOBLIN-MALE-BUTTPLUG").concat(maleDefeatCont), b.branch(false).concat(maleDefeatCont)),
					b.branch(Outcome.SATISFIED_ANAL).textScene("GOBLIN-MALE-SATISFIED-ANAL"),
					b.branch(Outcome.SATISFIED_ORAL).textScene("GOBLIN-MALE-SATISFIED-ORAL")	
				);
			case GOBLIN:
				Branch analCont = b.branch().textScene("GOBLIN-ANAL-CONT").checkScene(
					Stat.ENDURANCE, 
					b.branch(3).textScene("GOBLIN-FIGHTOFF"),
					b.branch(0).textScene("GOBLIN-SECONDS").checkScene(
						Stat.ENDURANCE,
						b.branch(2).textScene("GOBLIN-FIGHTOFF"),
						b.branch(0).textScene("GOBLIN-THIRDS").checkScene(
							Stat.ENDURANCE, 
							b.branch(1).textScene("GOBLIN-FIGHTOFF"),
							b.branch(0).textScene("GOBLIN-FOURTHS")
						)
					)
				);
				
				Branch postVirginityCheck = b.branch().choiceScene(
					"Mouth, or ass?",
					b.branch("In the Mouth").textScene("GOBLIN-MOUTH"),
					b.branch("Up The Ass").textScene("GOBLIN-ANAL").checkScene(CheckType.PLUGGED, b.branch(true).textScene("GOBLIN-BUTTPLUG").concat(analCont), b.branch(false).concat(analCont))
				);
				Branch[] battleScenes = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("GOBLIN-VICTORY").choiceScene("Hrm...", b.branch("Give her the D").textScene("GOBLIN-VICTORY-TOP"), b.branch("Inspect her").choiceScene("Ride ze gobbo?", b.branch("Gobbo's waiting!").textScene("GOBLIN-VICTORY-BOTTOM").require(ChoiceCheckType.LEWD), b.branch("No")), b.branch("Leave")), 
					b.branch(Outcome.DEFEAT).textScene("GOBLIN-DEFEAT").checkScene(
						CheckType.GOBLIN_VIRGIN,
						b.branch(true).textScene("GOBLIN-VIRGIN").concat(postVirginityCheck),
						b.branch(false).textScene("GOBLIN-EXPERT").concat(postVirginityCheck)
					),
					b.branch(Outcome.SATISFIED_ANAL).textScene("GOBLIN-SATISFIED-ANAL"),
					b.branch(Outcome.SATISFIED_ORAL).textScene("GOBLIN-SATISFIED-ORAL")	
				};
				Branch pantsCutDown = b.branch(0).textScene("GOBLIN-PANTS-DOWN").battleScene(BattleCode.GOBLIN, Stance.DOGGY_BOTTOM, Stance.DOGGY, battleScenes);
				
				Branch cutPants = b.branch().textScene("GOBLIN-POST-SPEAR").choiceScene(
					"Quick, what do you do?",
					b.branch("Catch Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 5).textScene("GOBLIN-CATCH").choiceScene(
						"What do you do with her?",
						b.branch("Put Her Down").textScene("GOBLIN-RELEASE").choiceScene(
							"Accept Her Offer?",
							b.branch("Accept").textScene("GOBLIN-ACCEPT"),
							b.branch("Decline").textScene("GOBLIN-DECLINE")
						),
						b.branch("Turn Her Over Your Knee").textScene("GOBLIN-FLIP").checkScene(
							Stat.STRENGTH,
							b.branch(5).textScene("GOBLIN-SPANK"),
							b.branch(0).textScene("GOBLIN-GETBIT")
						)
					),
					b.branch("Trip Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 4).textScene("GOBLIN-TRIP").choiceScene(
						"What do you do?",
						b.branch("Attack").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.PRONE,
							battleScenes
						),
						b.branch("Run").textScene("GOBLIN-FLEE")
					),
					b.branch("Disarm Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 3).textScene("GOBLIN-DISARM").choiceScene(
						"What do you do?",
						b.branch("Attack Her").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.BALANCED, true,
							battleScenes
						),
						b.branch("Block Her").battleScene(
							BattleCode.GOBLIN, Stance.BALANCED, Stance.BALANCED, true,
							battleScenes
						),
						b.branch("Let Her Go")
					),
					b.branch("Avoid Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 2).textScene("GOBLIN-DODGE").choiceScene(
						"What do you do?",
						b.branch("Attack Her").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.BALANCED,
							battleScenes
						),
						b.branch("Block Her").battleScene(
							BattleCode.GOBLIN, Stance.BALANCED, Stance.BALANCED,
							battleScenes
						),
						b.branch("Let Her Go")
					),
					b.branch("Nothing").concat(pantsCutDown)
				);
				
				Branch[] goblinStrength = new Branch[]{b.branch(5).textScene("GOBLIN-SPEAR-STEAL").concat(cutPants),
						b.branch(0).textScene("GOBLIN-SPEAR-DROP").concat(cutPants)};
				Branch[] goblinSpear = new Branch[]{
					b.branch(7).textScene("GOBLIN-SPEAR-GRAB").checkScene(
						Stat.STRENGTH, 
						goblinStrength
					),
					b.branch(5).textScene("GOBLIN-SPEAR-DODGE").concat(cutPants), 
					b.branch(0).textScene("GOBLIN-SPEAR-STABBED").concat(cutPants)
				};
				
				return b.branch().checkScene(
					CheckType.GOBLIN_BIRTH, 
					b.branch(true).textScene("GOBLIN-BIRTH").checkScene(CheckType.GOBLIN_BIRTH_HARPY, b.branch(true).textScene("GOBLIN-BIRTH-HARPY"), b.branch(false).checkScene(CheckType.GOBLIN_BIRTH_WEREWOLF, b.branch(true).textScene("GOBLIN-BIRTH-WEREWOLF"), b.branch(false).textScene("GOBLIN-BIRTH-CENTAUR"))), 
					b.branch(false).checkScene(
						CheckType.GOBLIN_KNOWN,
						b.branch(true).textScene("GOBLIN-INTRO").choiceScene(
							"What path do you follow?",
							b.branch("Pass By").textScene("GOBLIN-PASSBY"),
							b.branch("Enter the Small Path").textScene("GOBLIN-ENTRANCE").checkScene(
								Stat.PERCEPTION, 
								b.branch(7).textScene("GOBLIN-EAGLE-EYE").checkScene(
									Stat.AGILITY,
									b.branch(5).textScene("GOBLIN-SPEAR-GRAB").checkScene(
										Stat.STRENGTH,
										goblinStrength
									),
									b.branch(3).textScene("GOBLIN-SPEAR-DODGE").concat(cutPants),
									b.branch(0).textScene("GOBLIN-SPEAR-STABBED").concat(cutPants)
								),
								b.branch(4).textScene("GOBLIN-CONTRAPTION").checkScene(
									Stat.AGILITY,
									goblinSpear
								),
								b.branch(0).textScene("GOBLIN-AMBUSH").checkScene(
									Stat.AGILITY, 
									b.branch(5).textScene("GOBLIN-LOG-DODGE").checkScene(
										Stat.AGILITY, goblinSpear
									),
									b.branch(0).checkScene(
										CheckType.WEARING_HELMET, 
										b.branch(true).textScene("GOBLIN-HELMET").checkScene(Stat.AGILITY,goblinSpear), 
										b.branch(false).checkScene(
											Stat.ENDURANCE,
											b.branch(7).textScene("GOBLIN-OBLIVIOUS"),
											b.branch(0).textScene("GOBLIN-TOTALFAIL")
										)
									)
								)
							)
						),	
						b.branch(false).textScene("GOBLIN-SECOND-QUEST").choiceScene(
							"Brave the sign?", 
							b.branch("Pass the sign").textScene("GOBLIN-REUNION").battleScene(
								BattleCode.GOBLIN,
								battleScenes
							), 
							b.branch("Go another way")
						) 	
					)
				);
			case GOLEM:
				Branch golemHypnosis = b.branch().textScene("GOLEM-HYPNOSIS");
				Branch golemMisunderstanding = b.branch(0).textScene("GOLEM-MISUNDERSTANDING").choiceScene(
						"Where will she dump her semen tanks?",
						b.branch("In your asshole").concat(golemHypnosis),
						b.branch("Into your rectum").concat(golemHypnosis),
						b.branch("Up your shitter").concat(golemHypnosis),
						b.branch("Your gut (anally)").concat(golemHypnosis),
						b.branch("Buttsex, creampie").concat(golemHypnosis),
						b.branch("Cummies for tummies").concat(golemHypnosis)
					);
				Branch[] golemBattleOutcomes = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("GOLEM-VICTORY").choiceScene(
						"What do you do?", 
						b.branch("Ask for help").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
						b.branch("Ask for... help").require(ChoiceCheckType.LEWD).textScene("GOLEM-TOP"),
						b.branch("Bid her farewell").textScene("GOLEM-FREE")
					), 
					b.branch(Outcome.DEFEAT).textScene("GOLEM-DEFEAT").concat(golemMisunderstanding)
				};
				Branch golemBattle = b.branch().battleScene(
					BattleCode.GOLEM, 
					golemBattleOutcomes
				);
				Branch[] calmOptions = new Branch[]{
					b.branch("Ask for help").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
					b.branch("Ask about her").textScene("GOLEM-SPEAK").choiceScene(
						"What do you ask of her?",
						b.branch("Ask for help").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
						b.branch("Ask for... help").require(ChoiceCheckType.LEWD).textScene("GOLEM-TOP"),
						b.branch("Ask to fight her").concat(golemBattle),
						b.branch("Bid her farewell").textScene("GOLEM-FREE")
					),
					b.branch("Ask to fight her").concat(golemBattle),
					b.branch("Bid her farewell").textScene("GOLEM-FREE")
				};
				
				return b.branch().textScene("GOLEM-INTRO").checkScene(
					Stat.MAGIC,
					b.branch(1).textScene("GOLEM-AWARE").choiceScene(
						"Touch the statue?", 
						b.branch("Touch the statue").textScene("GOLEM-AWAKEN").choiceScene( 
							"Her energies are in flux!", 
							b.branch("Dominate Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 5).textScene("GOLEM-DOMINATED").choiceScene(
								"What do you will of her?", 
								b.branch("Demand tribute").textScene("GOLEM-TRIBUTE"),
								b.branch("Demand pleasure").checkScene(Perk.ANAL_ADDICT, b.branch(2).textScene("GOLEM-TOP"), golemMisunderstanding),
								b.branch("Ask to fight her").concat(golemBattle),
								b.branch("Tell her to shut down").textScene("GOLEM-SHUTDOWN")
							),
							b.branch("Soothe Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 5).textScene("GOLEM-SOOTHED").choiceScene("What do you do?", calmOptions),
							b.branch("Calm Her").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 3).textScene("GOLEM-CALMED").checkScene(
								Stat.PERCEPTION, 
								b.branch(4).choiceScene("What do you do?", calmOptions), 
								golemMisunderstanding
							),
							b.branch("Brace Yourself").textScene("GOLEM-OVERDRIVE").battleScene(
								BattleCode.GOLEM, Stance.BALANCED, Stance.CASTING,
								golemBattleOutcomes
							)
						),
						b.branch("Pull your hand away").textScene("GOLEM-LEAVE")
					),
					b.branch(0).textScene("GOLEM-UNAWARE")				
				);			
			case HARPY:
				Branch harpyMarriage = b.branch().textScene("HARPY-MARRIAGE").gameEnd();
				Branch[] battleBranches = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("HARPY-VICTORY").choiceScene("What do you do?", b.branch("Fuck her ass").require(ChoiceCheckType.FREE_COCK).textScene("HARPY-VICTORY-TOP-ANAL"), b.branch("Sit on her dick").require(ChoiceCheckType.LEWD).textScene("HARPY-VICTORY-BOTTOM-ANAL"), b.branch("Leave")), 
					b.branch(Outcome.DEFEAT).checkScene(
						CheckType.PLUGGED, 
						b.branch(true).textScene("HARPY-PLUGGED"),
						b.branch(false).textScene("HARPY-DEFEAT").checkScene(
							Perk.CUCKOO_FOR_CUCKOO, 
							b.branch(3).textScene("HARPY-LOVE-BIRD").concat(harpyMarriage), 
							b.branch(0).checkScene(Perk.ANAL_ADDICT, b.branch(3).textScene("HARPY-LOVE-ANAL").concat(harpyMarriage), b.branch(0).textScene("HARPY-FINISH"))
						)), 
					b.branch(Outcome.SATISFIED_ANAL).textScene("HARPY-SATISFIED-ANAL"),
					b.branch(Outcome.SATISFIED_ORAL).textScene("HARPY-SATISFIED-ORAL")
				};
				Branch harpyDodge = b.branch(6).textScene("HARPY-DODGE").battleScene(
					BattleCode.HARPY, Stance.BALANCED, Stance.PRONE,
					battleBranches
				);
				return b.branch().textScene("HARPY-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2, 
					b.branch(true).concat(harpyDodge),
					b.branch(false).checkScene(
						Stat.AGILITY,
						harpyDodge,
						b.branch(4).textScene("HARPY-DUCK").battleScene(
							BattleCode.HARPY, Stance.KNEELING, Stance.BALANCED,
							battleBranches
						),
						b.branch(0).textScene("HARPY-HORK").battleScene(
							BattleCode.HARPY, Stance.FELLATIO_BOTTOM, Stance.FELLATIO,
							battleBranches
						) 
					)
			    );
			case HARPY_STORY:
				return b.branch().textScene("STORY-HARPY").battleScene(
					BattleCode.HARPY_STORY,
					b.branch(Outcome.VICTORY).textScene("STORY-HARPY-VICTORY").choiceScene("Stuff the bird?", b.branch("Flip the bird").textScene("STORY-HARPY-VICTORY-BUTTSEX"), b.branch("Walk away")), 
					b.branch(Outcome.DEFEAT).textScene("STORY-HARPY-DEFEAT").gameEnd(), 
					b.branch(Outcome.KNOT_ANAL).textScene("STORY-HARPY-ANAL"), 
					b.branch(Outcome.KNOT_ORAL).textScene("STORY-HARPY-ORAL")
				);
			case INITIAL:
				return b.branch().textScene("INTRO").gameTypeScene(
					b.branch("Create Character").textScene("CHARACTER-CREATE").bonusSelection().characterCreation(false).skillSelection().characterCustomization(),
					b.branch("Story (Patrons)").textScene("STORY-MODE")
				); 	
			case INN_MONSTER:
				return b.branch().textScene("INN-MONSTER").choiceScene(
					"Stay the night?",
					b.branch("Rest at Inn").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("INN-MONSTER-STAY"),
					b.branch("Leave")
				);
			case INN:
				Branch afterScene = b.branch().textScene("INNKEEP-10");  
				Branch leave = b.branch("Leave");
				return b.branch().textScene("INNKEEP-01").choiceScene(
					"Stay the night?",
					b.branch("Rest at Inn").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("INNKEEP-02"),
					b.branch("Rest at Inn (Low Funds)").require(ChoiceCheckType.GOLD_LESS_THAN_X, 10).checkScene(
						CheckType.INN_0,
						b.branch(true).textScene("INNKEEP-03").choiceScene(
							"Take his offer?",
							b.branch("Get under the table").textScene("INNKEEP-04"),
							leave
						),
						b.branch(false).checkScene(
							CheckType.INN_1,
							b.branch(true).textScene("INNKEEP-07").choiceScene(
								"Take his offer?",
								b.branch("Go to his room").textScene("INNKEEP-08").checkScene(
									CheckType.VIRGIN, 
									b.branch(true).textScene("INNKEEP-09").concat(afterScene), 
									b.branch(false).concat(afterScene)
								),
								leave
							),
							b.branch(false).checkScene(
								CheckType.INN_2,
								b.branch(true).textScene("INNKEEP-12").choiceScene(
									"Take his offer?",
									b.branch("Join him").textScene("INNKEEP-13"),
									leave
								), 
								b.branch(false).textScene("INNKEEP-16").choiceScene(
									"Take his offer?",
									b.branch("Marry him").textScene("INNKEEP-17").gameEnd(),
									leave
								)
							)
						)
					),
					leave
			    );
			case LEVEL_UP:
				if (mode == GameMode.STORY) {
					return b.branch().textScene("NO-SKILLS"); 	
				}
				else {
					return b.branch().skillSelection(); 	
				}
			case MERI_COTTAGE:
				return b.branch().checkScene(CheckType.MERI_VISITED, b.branch(true).textScene("STORY-WITCH-COTTAGE-VISIT"), b.branch(false).textScene("STORY-WITCH-COTTAGE")); 	
			case MERMAID:
				Branch mermaidLossEggfill = b.branch(Outcome.SUBMISSION).textScene("MERMAID-EGGFILL").choiceScene(
					"Where does she lay her eggs?", 
					b.branch("Don't lay eggs in my ass!").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("MERMAID-FACEEGG"),
					b.branch("In your ass").textScene("MERMAID-ASSEGG")
				);
				Branch mermaidBattle = b.branch().battleScene(
					BattleCode.MERMAID,
					b.branch(Outcome.VICTORY).textScene("MERMAID-VICTORY"),
					b.branch(Outcome.DEFEAT).textScene("MERMAID-DEFEAT").checkScene(
						CheckType.FREE_COCK,
						b.branch(true).checkScene(CheckType.USABLE_COCK, b.branch(true).textScene("MERMAID-MOUNT").concat(mermaidLossEggfill), b.branch(false).textScene("MERMAID-FLACCID-FRUSTRATED")), 
						b.branch(false).textScene("MERMAID-CAGE-FRUSTRATED")
					),
					mermaidLossEggfill
				);
				Branch askForSex = b.branch("Ask to fuck her").require(ChoiceCheckType.FREE_COCK).checkScene(CheckType.IS_EGGED, b.branch(true).textScene("MERMAID-EGGSLUT"), b.branch(false).textScene("MERMAID-FUCK").choiceScene("Well?", b.branch("Yes").textScene("MERMAID-EGGTIME"), b.branch("No").textScene("MERMAID-SCORNED").concat(mermaidBattle)));				
				Branch secondQuestion = b.branch().choiceScene(
					"Agree to fuck her?",
					b.branch("Ask to pass").checkScene(
						Stat.CHARISMA, 
						b.branch(7).textScene("MERMAID-REGRETFUL"), 
						b.branch(3).textScene("MERMAID-LETPASS"), 
						b.branch(0).textScene("MERMAID-DENIED").concat(mermaidBattle)
					), 
					askForSex
				);
				Branch mermaidMeeting = b.branch().textScene("MERMAID-MEETING").choiceScene(
					"What do you ask of her?", 
					b.branch("Ask to pass").checkScene(
						Stat.CHARISMA, 
						b.branch(7).textScene("MERMAID-CHARMED").concat(secondQuestion), 
						b.branch(3).textScene("MERMAID-WARNING").concat(secondQuestion), 
						b.branch(0).textScene("MERMAID-ULTIMATUM").concat(secondQuestion)
					), 
					askForSex
				);
				Branch moreEggs = b.branch().choiceScene(
					"Fertilize and lay more eggs?",
					b.branch("Get egged").require(ChoiceCheckType.FREE_COCK).textScene("MERMAID-SECONDS"),
					b.branch("Uhh... no").textScene("MERMAID-SECONDS-DENIED")
				);
				
				return b.branch().textScene("MERMAID-INTRO").checkScene(
					CheckType.MERMAID_FIRST_ENCOUNTER, 
					b.branch(true).textScene("MERMAID-FIRST-TIME").checkScene(Stat.ENDURANCE, b.branch(6).textScene("MERMAID-SWIMHERO").concat(mermaidMeeting), b.branch(4).textScene("MERMAID-SWIM").concat(mermaidMeeting), b.branch(0).textScene("MERMAID-SWIMFAIL").concat(mermaidMeeting)), 
					b.branch(false).checkScene(
						CheckType.MERMAID_TRYAGAIN, 
						b.branch(true).textScene("MERMAID-REVISIT").concat(secondQuestion), 
						b.branch(false).checkScene(
							CheckType.MERMAID_ATTACK_ON_SIGHT, 
							b.branch(true).textScene("MERMAID-ATTACK").choiceScene("What do you do?", b.branch("Fight").concat(mermaidBattle), askForSex), 
							b.branch(false).checkScene(CheckType.MERMAID_EGG_HATCH, 
								b.branch(true).textScene("MERMAID-EGG-HATCH").concat(moreEggs), 
								b.branch(false).checkScene(
									CheckType.MERMAID_HATCHED, 
									b.branch(true).checkScene(CheckType.IS_EGGED, b.branch(true).textScene("MERMAID-OTHEREGG-VISIT"), b.branch(false).textScene("MERMAID-HATCHED-VISIT").concat(moreEggs)), 
									b.branch(false).checkScene(CheckType.MERMAID_EGG_ACCIDENTAL_HATCH, b.branch(true).textScene("MERMAID-HATCH-ACCIDENT"), b.branch(false).textScene("MERMAID-EGG-VISIT"))
								)
							)
						)
					)
				);
			case MOUTH_FIEND_ESCAPE:
				return b.branch().textScene("MOUTHFIEND-ESCAPE-INTRO").choiceScene(
					"Stay on the road?", 
					b.branch("Stay on the road").textScene("MOUTHFIEND-ROAD").choiceScene("What do you do?", b.branch("Sneak past").textScene("MOUTHFIEND-SNEAK"), b.branch("Wait").textScene("MOUTHFIEND-WAIT")),
					b.branch("Brave the forest").textScene("MOUTHFIEND-FOREST").gameEnd()
				);
			case MOUTH_FIEND:
				Branch mouthfiendEnd = b.branch().textScene("MOUTHFIEND-END").gameEnd(GameOver.MOUTH_FIEND);
				Branch tongueDay = b.branch().textScene("MOUTHFIEND-TONGUEDAY").choiceScene("How do you want it?", b.branch("Deep").textScene("MOUTHFIEND-DEEP").concat(mouthfiendEnd), b.branch("Hard").textScene("MOUTHFIEND-HARD").concat(mouthfiendEnd));
				Branch failedEscape = b.branch().textScene("MOUTHFIEND-FAILEDESCAPE").concat(tongueDay);
				Branch afterFourthQuestion = b.branch().textScene("MOUTHFIEND-AFTERQUESTIONS").choiceScene(
					"What do you do?", 
					b.branch("Pretend to reshackle yourself").textScene("MOUTHFIEND-GAMBIT"),
					b.branch("Actually reshackle yourself").textScene("MOUTHFIEND-SUBMISSION").concat(tongueDay),
					b.branch("Try to break the door open").textScene("MOUTHFIEND-BREAKDOOR").concat(tongueDay)
				);
				Branch afterThirdQuestion = b.branch().choiceScene(
						"What do you ask her?", 
						b.branch("How much do your balls weigh?").textScene("MOUTHFIEND-WEIGH").concat(afterFourthQuestion), 
						b.branch("How much can you cum?").textScene("MOUTHFIEND-HOWMUCH").concat(afterFourthQuestion),  
						b.branch("What if I need something from you?").textScene("MOUTHFIEND-HOWCALL").concat(afterFourthQuestion), 
						b.branch("How can I get out of this?").textScene("MOUTHFIEND-HOWLEAVE").concat(afterFourthQuestion),
						b.branch("You're so pretty").textScene("MOUTHFIEND-FLATTER").concat(afterFourthQuestion)
					);
				Branch afterSecondQuestion = b.branch().choiceScene(
					"What do you ask her?", 
					b.branch("I have to use the restroom").textScene("MOUTHFIEND-WHEREREST").concat(afterThirdQuestion), 
					b.branch("You won't get away with this!").textScene("MOUTHFIEND-BOLD").concat(afterThirdQuestion),  
					b.branch("Mouthfuckersayswhat").textScene("MOUTHFIEND-SAYSWHAT").concat(afterThirdQuestion), 
					b.branch("*Open Wide*").textScene("MOUTHFIEND-OPENSEDUCTION").concat(afterThirdQuestion)
				);
				Branch afterFirstQuestion = b.branch().choiceScene(
					"What do you ask her?", 
					b.branch("I want to suck your cock").textScene("MOUTHFIEND-WANTSUCK").concat(afterSecondQuestion), 
					b.branch("Can I have something to eat?").textScene("MOUTHFIEND-WHATEAT").concat(afterSecondQuestion),  
					b.branch("What's in the wilderness?").textScene("MOUTHFIEND-WHATWILD").concat(afterSecondQuestion), 
					b.branch("Release me at once!").textScene("MOUTHFIEND-DEMANDRELEASE").concat(afterSecondQuestion)
				);
				Branch afterMarriageQuestion = b.branch().textScene("MOUTHFIEND-INSPECTION").choiceScene(
					"What do you ask her?", 
					b.branch("Why... in the mouth?").textScene("MOUTHFIEND-WHYMOUTH").concat(afterFirstQuestion), 
					b.branch("Can you NOT fuck my mouth?").textScene("MOUTHFIEND-NOTMOUTH").concat(afterFirstQuestion),  
					b.branch("Is this a dream?").textScene("MOUTHFIEND-DREAM").concat(afterFirstQuestion), 
					b.branch("What's the passcode?").textScene("MOUTHFIEND-PASSCODE").concat(afterFirstQuestion)
				);
				
				
				Branch secondThoughts = b.branch().textScene("MOUTHFIEND-SECOND-THOUGHTS").concat(afterMarriageQuestion);
				return b.branch().checkScene(
					CheckType.MOUTH_FIEND_CASTLE, 
					b.branch(true).textScene("MOUTHFIEND-INTRO").choiceScene(
						"Marry her?", 
						b.branch("Yes").textScene("MOUTHFIEND-MARRIAGE-YES").checkScene(
							CheckType.HIGH_DIGNITY, 
							b.branch(true).textScene("MOUTHFIEND-HIGH-DIGNITY").concat(afterMarriageQuestion),
							b.branch(false).textScene("MOUTHFIEND-LOW-DIGNITY").choiceScene("Seduce her?", b.branch("Stick out your tongue").textScene("MOUTHFIEND-TONGUE-OUT").checkScene(CheckType.ANY_DIGNITY, b.branch(true).concat(secondThoughts), b.branch(false).textScene("MOUTHFIEND-NO-DIGNITY").gameEnd(GameOver.MOUTH_FIEND)), b.branch("Close your mouth").concat(secondThoughts))
						), 
						b.branch("No").textScene("MOUTHFIEND-MARRIAGE-NO").concat(afterMarriageQuestion),
						b.branch("Insult her").textScene("MOUTHFIEND-INSULT").concat(afterMarriageQuestion)
					), 
					b.branch(false).concat(failedEscape)
				);
			case NAGA:
				return b.branch().textScene("NAGA-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_3, 
					b.branch(true).textScene("NAGA-SPOTTED").battleScene(
						BattleCode.NAGA, 
						b.branch(Outcome.VICTORY).textScene("NAGA-VICTORY").choiceScene(
							"What do you do with her?", 
							b.branch("In her... cloaca").require(ChoiceCheckType.FREE_COCK).textScene("NAGA-CLOACA"), 
							b.branch("In her mouth").require(ChoiceCheckType.FREE_COCK).textScene("NAGA-FELLATIO").gameEnd(), 
							b.branch("Nothing")
						), 
						b.branch(Outcome.DEFEAT).textScene("NAGA-DEFEAT").gameEnd(), 
						b.branch(Outcome.DEATH).textScene("NAGA-CRUSHED").gameEnd()
					), 
					b.branch(false).textScene("NAGA-AMBUSH").choiceScene(
						"Front or back?", 
						b.branch("Front").textScene("NAGA-IRRUMATIO"), 
						b.branch("Back").textScene("NAGA-CLOACALICK")
					)
				);
			case OGRE:
				Branch passerby = b.branch().textScene("OGRE-PASSERBY");
				Branch partingScene = b.branch().checkScene(
					Perk.SIZE_QUEEN,
					b.branch(3).textScene("OGRE-MARRY").gameEnd(), 
					b.branch(2).textScene("OGRE-HARDSELL").concat(passerby),  
					b.branch(1).textScene("OGRE-FLIRT").concat(passerby), 
					b.branch(0).concat(passerby)
				);
				Branch[] battleOutcomes = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("OGRE-VICTORY").concat(b.branch().textScene("OGRE-VICTORY-GOLD")),
					b.branch(Outcome.SATISFIED_ANAL).textScene("OGRE-SATISFIED").concat(partingScene),
					b.branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene)
				};
				
				Branch ogreFirstBattle = b.branch().battleScene(
					BattleCode.OGRE, 
					battleOutcomes
				);
				Branch ogreFirstBattleDisarm = b.branch().battleScene(BattleCode.OGRE, Stance.BALANCED, Stance.BALANCED, true, b.branch(Outcome.VICTORY).textScene("OGRE-VICTORY").concat(b.branch().textScene("OGRE-VICTORY-GOLD")), b.branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene), b.branch(Outcome.SATISFIED_ANAL).textScene("OGRE-SATISFIED").concat(partingScene));
				Branch ogreSecondBattle = b.branch().battleScene(BattleCode.OGRE, b.branch(Outcome.VICTORY).textScene("OGRE-VICTORY"), b.branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene), b.branch(Outcome.SATISFIED_ANAL).textScene("OGRE-SATISFIED").concat(partingScene));
				Branch grabbedByOgre = b.branch().textScene("OGRE-GRABBED").checkScene(
					Stat.ENDURANCE,
					b.branch(4).textScene("OGRE-ENDURE"), 
					b.branch(0).gameEnd()
				);
				
				return b.branch().textScene("OGRE-INTRO").checkScene(
					CheckType.OGRE_DONE,
					b.branch(true).textScene("OGRE-ENTRANCE").checkScene(
						Stat.PERCEPTION, 
						b.branch(3).textScene("OGRE-SPOTTED").choiceScene(
							"Do you attempt to steal from the ogre or ambush him?",
							b.branch("Steal").textScene("OGRE-STEALTH").checkScene(
								Stat.AGILITY,
								b.branch(7).textScene("OGRE-STEAL"),
								b.branch(5).textScene("OGRE-WAKE").concat(ogreFirstBattle),
								b.branch(0).concat(grabbedByOgre)
							), 
							b.branch("Ambush").textScene("OGRE-STEALTH").checkScene(
								Stat.AGILITY, 
								b.branch(5).choiceScene(
									"Pre-emptive ranged attack or kick away his club?",
									b.branch("Ranged Attack").battleScene(BattleCode.OGRE, battleOutcomes).setRange(2),
									b.branch("Kick Away Club").concat(ogreFirstBattleDisarm)
								),
								b.branch(0).textScene("OGRE-WAKE2").concat(ogreFirstBattle)
							),
							b.branch("Leave")
						),
						b.branch(0).textScene("OGRE-SURPRISE").concat(grabbedByOgre)
					),
					b.branch(false).textScene("OGRE-BATTLE").concat(ogreSecondBattle)
					
			    );			
			case OGRE_STORY:
				return b.branch().textScene("STORY-OGRE").battleScene(
					BattleCode.OGRE_STORY,
					b.branch(Outcome.VICTORY).textScene("STORY-OGRE-VICTORY"), 
					b.branch(Outcome.DEFEAT).textScene("STORY-OGRE-DEFEAT").gameEnd(),
					b.branch(Outcome.SATISFIED_ANAL).textScene("STORY-OGRE-DEFEAT").gameEnd()
				);
			case OGRE_WARNING_STORY:
				return b.branch().textScene("OGRE-WARN");
			case ORC:
				Branch leaveOrc = b.branch().textScene("ORC-LEAVE");
				Branch oralScene = b.branch().textScene("ORC-ORAL");
				Branch failedCharisma = b.branch(0).textScene("ORC-OFFER-FAIL").concat(oralScene);
				Branch orcAnal = b.branch().textScene("ORC-ANAL").checkScene(CheckType.PLUGGED, b.branch(true).textScene("ORC-ANAL-PLUGGED").textScene("ORC-ANAL-CONTINUE"), b.branch(false).textScene("ORC-ANAL-CONTINUE"));
				Branch battleVictory = b.branch().textScene("ORC-VICTORY").choiceScene(
					"Front, back, or decline?", 
					b.branch("Front").require(ChoiceCheckType.LEWD).concat(orcAnal),
					b.branch("Back").require(ChoiceCheckType.FREE_COCK).textScene("ORC-BOTTOM"),
					b.branch("Decline").textScene("ORC-DECLINE")
				);
				
				Branch firstBattle = b.branch("Take a fighting stance").battleScene(
					BattleCode.ORC, 4,
					b.branch(Outcome.VICTORY).textScene("ORC-VICTORY1").concat(battleVictory),
					b.branch(Outcome.DEFEAT).textScene("ORC-DEFEAT").choiceScene(
						"What do you offer?",
						b.branch("Anal").require(ChoiceCheckType.LEWD).concat(orcAnal),
						b.branch("Oral").textScene("ORC-OFFER-ORAL").concat(oralScene), 
						b.branch("Nasal").textScene("ORC-NASAL"),
						b.branch("Penal").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 6).textScene("ORC-PENAL"),
						b.branch("Facial").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 4).textScene("ORC-FACIAL"),
						b.branch("Nothing").concat(failedCharisma)
					),
					b.branch(Outcome.SATISFIED_ANAL).textScene("ORC-SATISFIED")
				);
				
				return b.branch().textScene("ORC-INTRO").checkScene(
					CheckType.ORC_ENCOUNTERED,
					b.branch(true).textScene("ORC-OLDMEN").choiceScene(
						"Do you speak up?",
						b.branch("Speak up").textScene("ORC-VIEW").choiceScene( 
							"How do you respond?", 
							firstBattle,
							b.branch("Remain still").textScene("ORC-STILL").concat(leaveOrc) 
						), 
						b.branch("Remain silent").textScene("ORC-SILENT").concat(leaveOrc) 
					),
					b.branch(false).checkScene(
						CheckType.ORC_BRAVE, 
						b.branch(true).textScene("ORC-REUNION").choiceScene(
							"Accept her invitation?",
							b.branch("Accept").require(ChoiceCheckType.LEWD).textScene("ORC-REUNION-ACCEPT"),
							b.branch("Offer to spar").concat(firstBattle),
							b.branch("Decline").textScene("ORC-REUNION-DECLINE")
						),
						b.branch(false).textScene("ORC-COWARD-CALLOUT").choiceScene(
							"Well?",
							b.branch("Yes").require(ChoiceCheckType.LEWD).textScene("ORC-CATAMITE"),
							b.branch("No").textScene("ORC-ANGER").battleScene(
								BattleCode.ORC,
								b.branch(Outcome.VICTORY).textScene("ORC-VICTORY2").concat(battleVictory),
								b.branch(Outcome.DEFEAT).textScene("ORC-DENIGRATION").choiceScene(
									"Man or woman?", 
									b.branch("Man").textScene("ORC-BAD-END").gameEnd(),
									b.branch("Woman").textScene("ORC-WIFE").gameEnd()
								),
								b.branch(Outcome.SATISFIED_ANAL).textScene("ORC-VIOLATED").gameEnd()
							)
						)
					)
				);
			case ORC_STORY:
				return b.branch().textScene("STORY-ORC").battleScene(BattleCode.ORC_STORY, b.branch(Outcome.VICTORY).textScene("STORY-ORC-VICTORY"),  b.branch(Outcome.DEFEAT).textScene("STORY-ORC-DEFEAT").gameEnd(),  b.branch(Outcome.SATISFIED_ANAL).textScene("STORY-ORC-SATISFIED").gameEnd(),  b.branch(Outcome.SUBMISSION).textScene("STORY-ORC-SUBMISSION"));
			case SHOP:
				return b.branch().textScene("TOWN-SHOP").shopScene(ShopCode.SHOP);
			case SHOP_MONSTER:
				return b.branch().textScene("MONSTER-TOWN-SHOP").shopScene(ShopCode.MONSTER_SHOP);
			case SLIME:
				Branch loveDartCont = b.branch().textScene("SLIME-LOVEDART-CONT");
				return b.branch().textScene("SLIME-INTRO").choiceScene(
					"What do you do with the slime?",
					b.branch("Fight Her").battleScene(
						BattleCode.SLIME,
						b.branch(Outcome.VICTORY).textScene("SLIME-VICTORY").choiceScene(
							"Slay the slime?",
							b.branch("Stab the core").textScene("SLIME-STAB").checkScene(
								Stat.AGILITY,
								b.branch(6).textScene("SLIME-SHATTER"),
								b.branch(0).textScene("SLIME-FAIL").gameEnd()
							),
							b.branch("Spare her").textScene("SLIME-SPARE")						
						),
						b.branch(Outcome.DEFEAT).textScene("SLIME-DEFEAT").choiceScene(
							"What do you do?",
							b.branch("Try to speak").textScene("SLIME-MOUTH"),
							b.branch("Run!").checkScene(
								Stat.AGILITY, 
								b.branch(5).textScene("SLIME-FLEE"),
								b.branch(0).textScene("SLIME-FALL")								
							)
						)
					),
					b.branch("Smooch Her").textScene("SLIME-APPROACH").choiceScene(
						"Do you enter the slime, or...?",
						b.branch("Go In").require(ChoiceCheckType.FREE_COCK).textScene("SLIME-ENTER"),
						b.branch("Love Dart").require(ChoiceCheckType.LEWD).textScene("SLIME-LOVEDART").checkScene(CheckType.PLUGGED, b.branch(true).textScene("SLIME-BUTTPLUG").concat(loveDartCont), b.branch(false).concat(loveDartCont)),
						b.branch("Leave Her Be")
					),
					b.branch("Leave Her Be")			
				);
			case SPIDER:
				Branch spiderEscape = b.branch().textScene("SPIDER-ESCAPE");
				Branch spiderBattle = b.branch().battleScene(
					BattleCode.SPIDER, 
					b.branch(Outcome.VICTORY).textScene("SPIDER-VICTORY"),
					b.branch(Outcome.DEFEAT).textScene("SPIDER-DEFEAT").choiceScene("Pick your poison.",
						b.branch("Become her lover").checkScene(
							CheckType.IS_EGGED, 
							b.branch(true).textScene("SPIDER-NAH").gameEnd(), 
							b.branch(false).textScene("SPIDER-LOVER").textScene("SPIDER-OVIPOSITION").textScene("SPIDER-WIFE").checkScene(
								Stat.STRENGTH, 
								b.branch(6).textScene("SPIDER-STRENGTH-ESCAPE").concat(spiderEscape), 
								b.branch(0).checkScene(Stat.ENDURANCE, b.branch(5).textScene("SPIDER-ENDURANCE-ESCAPE"), b.branch(0).checkScene(Stat.MAGIC, b.branch(3).textScene("SPIDER-MAGIC-ESCAPE").concat(spiderEscape), b.branch(0).textScene("SPIDER-END").gameEnd()))
							)
						),
						b.branch("Become her dinner").textScene("SPIDER-BITE").gameEnd()
					),
					b.branch(Outcome.KNOT_ANAL).textScene("SPIDER-OVIPOSITION").textScene("SPIDER-NO-FERTILIZE").gameEnd()
				);
				
				Branch spiderBaby = b.branch().textScene("SPIDER-BABY").choiceScene(
					"What do you do?", 
					b.branch("Try to crush the tiny spider").checkScene(Stat.PERCEPTION, b.branch(6).textScene("SPIDER-AWARE").concat(spiderBattle), b.branch(0).textScene("SPIDER-AMBUSH").concat(spiderBattle)), 
					b.branch("Try to pet the tiny spider").textScene("SPIDER-GET").concat(spiderBattle), 
					b.branch("Leave the tiny spider alone").textScene("SPIDER-IGNORE").choiceScene("Do you fight or flee?", b.branch("Fight").concat(spiderBattle), b.branch("Flee").checkScene(Stat.AGILITY, b.branch(4).textScene("SPIDER-FLEE-PASS").checkScene(Stat.ENDURANCE, b.branch(4).textScene("SPIDER-FULL-FLEE"), b.branch(0).textScene("SPIDER-FLEE-FAIL").concat(spiderBattle)), b.branch(0).textScene("SPIDER-FLEE-FAIL").concat(spiderBattle)))
				);
				
				Branch webbedAndEgged = b.branch().textScene("SPIDER-UNCONSCIOUS").checkScene(CheckType.IS_EGGED, b.branch(true).textScene("SPIDER-UNCONSCIOUS-EGG-FEAST").gameEnd(), b.branch(false).textScene("SPIDER-UNCONSCIOUS-EGG-VIRGIN").gameEnd());
				Branch afterSigil = b.branch().textScene("SPIDER-WEBCAVE").checkScene(CheckType.WEARING_SHOES, b.branch(true).textScene("SPIDER-STEPWEB-SUCCESS").concat(spiderBaby), b.branch(false).textScene("SPIDER-STEPWEB-FAIL").concat(webbedAndEgged));
				Branch afterTrap1 = b.branch().textScene("SPIDER-SIGIL").choiceScene("Touch the sigil?", b.branch("Touch it").checkScene(Stat.MAGIC, b.branch(4).textScene("SPIDER-SIGIL-SUCCESS").concat(afterSigil), b.branch(2).textScene("SPIDER-SIGIL-PARTIAL").concat(afterSigil), b.branch(0).textScene("SPIDER-SIGIL-FAILURE").concat(afterSigil)), b.branch("Don't touch it").concat(afterSigil));
				Branch receiveTrap = b.branch().checkScene(CheckType.ALIVE, b.branch(true).concat(afterTrap1), b.branch(false).textScene("SPIDER-DROWN").concat(webbedAndEgged));
				Branch failTrap = b.branch().checkScene(Stat.ENDURANCE, b.branch(7).textScene("SPIDER-ENDURE").concat(receiveTrap), b.branch(4).textScene("SPIDER-PARTIAL-ENDURE").concat(receiveTrap), b.branch(0).textScene("SPIDER-FAIL-ENDURE").concat(receiveTrap));
				Branch afterRoom1 = b.branch().textScene("SPIDER-TRAP-APPROACH").checkScene(Stat.AGILITY, b.branch(7).textScene("SPIDER-AVOID-TRAP").concat(afterTrap1), b.branch(4).textScene("SPIDER-GRAB-TRAP").checkScene(CheckType.WEARING_GAUNTLETS, b.branch(true).textScene("SPIDER-GRAB-TRAP-SUCCESS").concat(afterTrap1), b.branch(false).textScene("SPIDER-GRAB-TRAP-FAIL").concat(failTrap)), b.branch(0).textScene("SPIDER-FAIL-TRAP").concat(failTrap));
				return b.branch().checkScene(CheckType.SPIDER_HATCH, b.branch(true).textScene("SPIDER-HATCH"), b.branch(false).checkScene(
					CheckType.SPIDER, 
					b.branch(true).textScene("SPIDER-INTRO").choiceScene(
						"What do you do?",
						b.branch("Traverse the Ruins").textScene("SPIDER-ENTER").choiceScene("Enter the room?", b.branch("Enter").textScene("SPIDER-ROOM").checkScene(Stat.PERCEPTION, b.branch(5).textScene("SPIDER-FIND1").concat(afterRoom1), b.branch(0).textScene("SPIDER-FIND1-FAIL").concat(afterRoom1)), b.branch("Pass by").concat(afterRoom1)),
						b.branch("Turn Back")
					),
					b.branch(false).textScene("SPIDER-REVISIT"))
				);
			case STARVATION:
				return b.branch().textScene("STARVATION-INTRO").checkScene(
					CheckType.VIRGIN,
					// if you're a virgin, it should mention it at the appropriate time
					b.branch(true).textScene("STARVATION").textScene("STARVATION-VIRGIN").textScene("STARVATION-FIRST-TIME"),
					b.branch(false).textScene("STARVATION").checkScene(Perk.BEASTMASTER, b.branch(3).textScene("STARVATION-GAME-OVER").gameEnd(), b.branch(2).textScene("STARVATION-WARNING"), b.branch(1).textScene("STARVATION-FIRST-TIME"), b.branch(0)) // can't get to Branch(0)
				);
			case STORY_FEM:
				return b.branch().textScene("STORY-FEM");
			case STORY_SIGN:
				return b.branch().textScene("CROSSROADS");
			case TAVERN:
				return b.branch().checkScene(
					CheckType.BROTHEL_QUEST_ACTIVE, 
						b.branch(true).textScene("TAVERN-MARK").choiceScene(
							"Take a carriage to the human town?", 
							b.branch("Yes").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("CARRIAGE-TO-HUMAN-TOWN"),
							b.branch("No")
						), 
						b.branch(false).textScene("TAVERN")
					);
			case TOWN:
				break;
			case TOWN2:
				break;
			case TOWN_CRIER:
				Array<String> squareOptions = new Array<String>();
				for (int ii = 0; ii < 5; ii++) { squareOptions.add("TOWN-SQUARE-" + ii); }
				
				Branch goodInfo = b.branch().textScene("TOWN-SQUARE-INFORMANT-GOOD");
				Branch payHimMore = b.branch().textScene("TOWN-SQUARE-INFORMANT-OKAY").checkScene(
						Stat.CHARISMA, 
						b.branch (4).concat(goodInfo), 
						b.branch(0).textScene("TOWN-SQUARE-INFORMANT-REQUEST").choiceScene("Deal?", b.branch("Pay 10 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("TOWN-SQUARE-INFORMANT-PAYMORE").concat(goodInfo), b.branch("Refuse"))
					);
				
				Branch payHim = b.branch().choiceScene(
					"Pay him for info?", 
					b.branch("Pay 20 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 20).textScene("TOWN-SQUARE-INFORMANT-PAID").concat(payHimMore), 
					b.branch("Offer... something else").textScene("TOWN-SQUARE-INFORMANT-ALTERNATIVE").choiceScene(
						"What do you do?", 
						b.branch("Bend over").require(ChoiceCheckType.LEWD).checkScene(Perk.PERFECT_BOTTOM, b.branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODANAL").textScene("TOWN-SQUARE-INFORMANT-OKAY").concat(goodInfo), b.branch(0).textScene("TOWN-SQUARE-INFORMANT-ANAL").concat(payHimMore)), 
						b.branch("Get on your knees").choiceScene(
							"What do you do on your knees?", 
							b.branch("Suck it").checkScene(Perk.BLOWJOB_EXPERT, b.branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODORAL").textScene("TOWN-SQUARE-INFORMANT-OKAY").concat(goodInfo), b.branch(0).textScene("TOWN-SQUARE-INFORMANT-ORAL").concat(payHimMore)), 
							b.branch("Stroke it").textScene("TOWN-SQUARE-INFORMANT-HAND").checkScene(Perk.CRANK_MASTER, b.branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODHAND").concat(payHimMore), b.branch(0).textScene("TOWN-SQUARE-INFORMANT-HAND").textScene("TOWN-SQUARE-INFORMANT-LIE"))
						),
						b.branch("Leave")
					),
					b.branch("Refuse")
				);
					
				Branch townSquareAnalLewd = b.branch().textScene("TOWN-SQUARE-LEWD-ANAL-ALLEY");
				Branch townSquareAnalChoice = b.branch(0).choiceScene(
					"How much?", 
					b.branch("20 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 7).textScene("TOWN-SQUARE-LEWD-ANAL-20").concat(townSquareAnalLewd), 
					b.branch("10 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("TOWN-SQUARE-LEWD-ANAL-10").concat(townSquareAnalLewd), 
					b.branch("5 gold").textScene("TOWN-SQUARE-LEWD-ANAL-5").concat(townSquareAnalLewd),
					b.branch("Decline")
				);
				
				Branch townSquareOralLewd = b.branch().textScene("TOWN-SQUARE-LEWD-ORAL-ALLEY");
				Branch townSquareOralChoice = b.branch(0).choiceScene(
					"Accept his offer?",
					b.branch("Yes").textScene("TOWN-SQUARE-LEWD-ORAL-SWITCH").choiceScene(
						"Get assfucked instead?", 
						b.branch("Blow him").textScene("TOWN-SQUARE-LEWD-ORAL-SWITCH-DECLINE").choiceScene(
							"How much?", 
							b.branch("15 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 7).textScene("TOWN-SQUARE-LEWD-ORAL-15").concat(townSquareOralLewd), 
							b.branch("7 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("TOWN-SQUARE-LEWD-ORAL-7").concat(townSquareOralLewd), 
							b.branch("3 gold").textScene("TOWN-SQUARE-LEWD-ORAL-3").concat(townSquareOralLewd),
							b.branch("Leave")
						), 
						b.branch("Take it in the ass").concat(townSquareAnalChoice)
					),
					b.branch("Nope")					
				);
				
				Branch townSquareHandyLewd = b.branch().textScene("TOWN-SQUARE-LEWD-HANDY-ALLEY");
				
				Branch townSquareOptions = b.branch().checkScene(
					CheckType.MOUTH_FIEND_DODGED, 
					b.branch(true).textScene("TOWN-SQUARE-SECOND-KIDNAP").choiceScene("Go with her?", b.branch("Go").textScene("TOWN-SQUARE-SECOND-KIDNAPPED"), b.branch("Decline").textScene("TOWN-SQUARE-SECOND-AVOID")),
					b.branch(false).checkScene(
						CheckType.MOUTH_FIEND_INTRO, 
						b.branch(true).textScene("TOWN-SQUARE-KIDNAP").checkScene(
							Stat.PERCEPTION, 
							b.branch(8).textScene("TOWN-SQUARE-KIDNAP-THWART"), 
							b.branch(0).textScene("TOWN-SQUARE-KIDNAP-SUCCESSFUL")
						), 
						b.branch(false).choiceScene(
							"What do you do?",
							b.branch("Eavesdrop").checkScene(
								CheckType.CRIER_QUEST, 
								b.branch(true).textScene("TOWN-SQUARE-INFORMANT").concat(payHim),
								b.branch(false).checkScene(
									CheckType.CRIER_REFUSE, 
									b.branch(true).textScene("TOWN-SQUARE-INFORMANT-RETURN").concat(payHim), 
									b.branch(false).checkScene(
										CheckType.RANDOM_LEWD_SCENE, 
										b.branch(true).checkScene(
											CheckType.LUCKY, 
											b.branch(true).checkScene(
												CheckType.LUCKY, 
												b.branch(true).textScene("TOWN-SQUARE-LEWD-TOP").checkScene(
													Perk.TOP, 
													b.branch(2).choiceScene("Fuck him?", b.branch("Let's go").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).require(ChoiceCheckType.FREE_COCK).textScene("TOWN-SQUARE-LEWD-TOP-ANAL"), b.branch("Nah")), 
													b.branch(0)
												), 
												b.branch(false).textScene("TOWN-SQUARE-LEWD-HANDY").choiceScene(
													"Accept his offer?",
													b.branch("Yes").textScene("TOWN-SQUARE-LEWD-HANDY-SWITCH").choiceScene(
														"Give a blowjob instead?", 
														b.branch("Give him a handjob").textScene("TOWN-SQUARE-LEWD-HANDY-SWITCH-DECLINE").choiceScene(
															"How much?", 
															b.branch("5 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 7).textScene("TOWN-SQUARE-LEWD-HANDY-5").concat(townSquareHandyLewd), 
															b.branch("3 gold").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("TOWN-SQUARE-LEWD-HANDY-3").concat(townSquareHandyLewd), 
															b.branch("1 gold").textScene("TOWN-SQUARE-LEWD-HANDY-1").concat(townSquareHandyLewd)
														), 
														b.branch("Blow him").concat(townSquareOralLewd)
													),
													b.branch("Nope")					
												)
											), 
											b.branch(false).checkScene(
												CheckType.LUCKY, 
												b.branch(true).textScene("TOWN-SQUARE-LEWD-ORAL").checkScene(
													Perk.MOUTH_MANIAC, 
													b.branch(2).checkScene(
														CheckType.ANY_WILLPOWER, 
														b.branch(true).textScene("TOWN-SQUARE-LEWD-ORAL-RESIST").concat(townSquareOralChoice), 
														b.branch(false).textScene("TOWN-SQUARE-LEWD-ORAL-FREE").concat(townSquareOralLewd)
													), 
													townSquareOralChoice
												), 
												b.branch(false).textScene("TOWN-SQUARE-LEWD-ANAL").checkScene(
													Perk.ANAL_ADDICT, 
													b.branch(2).checkScene(
														CheckType.ANY_WILLPOWER, 
														b.branch(true).textScene("TOWN-SQUARE-LEWD-ANAL-RESIST").concat(townSquareAnalChoice), 
														b.branch(false).textScene("TOWN-SQUARE-LEWD-ANAL-FREE").concat(townSquareAnalLewd)
													), 
													townSquareAnalChoice
												)
											)
										),
										b.branch(false).randomScene(squareOptions)
									)
								)
							),			
							b.branch("Listen to the town crier").checkScene(
								CheckType.CRIER, 
								b.branch(true).textScene("CRIER-NEW"), 
								b.branch(false).checkScene(CheckType.QUETZAL_DEFEATED, b.branch(true).textScene("CRIER-QUETZAL"), b.branch(false).textScene("CRIER-OLD"))
							)
						)
					)
				);	
				return b.branch().checkScene(CheckType.DAY, b.branch(true).textScene("TOWN-SQUARE-INTRO").concat(townSquareOptions), b.branch(false).textScene("TOWN-SQUARE-NIGHT"));
			case TOWN_STORY:
				Branch leaveTown = b.branch().textScene("STORY-007");
				return b.branch().textScene("STORY-005").shopScene(ShopCode.FIRST_STORY).textScene("STORY-006A").checkScene(
					Stat.CHARISMA,
					b.branch(6).textScene("STORY-006B").concat(leaveTown),
					b.branch(0).textScene("STORY-006C").concat(leaveTown)
				);
			case TRUDY_COMPANION:
				return b.branch().checkScene(CheckType.TRUDY_COMPANION1, 
						b.branch(true).textScene("TRUDY-COMPANION-FIRST").choiceScene("Spend time with Trudy?", b.branch("Spend time").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 8).textScene("TRUDY-COMPANION-HANGOUT"), b.branch("Not now")), 
						b.branch(false).checkScene(
							CheckType.TRUDY_COMPANION2, 
							b.branch(true).textScene("TRUDY-COMPANION-SECOND").choiceScene("Train with Trudy?", b.branch("Train").textScene("TRUDY-COMPANION-LEARN"), b.branch("Not now")),
							b.branch(false).textScene("TRUDY-COMPANION-REPEAT")
						)
					);
			case WARLOCK: 
				Branch warlockBattle = b.branch().battleScene(
					BattleCode.WARLOCK, 
					b.branch(Outcome.VICTORY).textScene("WARLOCK-VICTORY"), 
					b.branch(Outcome.DEFEAT).textScene("WARLOCK-DEFEAT").choiceScene(
						"What do you choose?", 
						b.branch("To love ass-sex").checkScene(Perk.ANAL_ADDICT, b.branch(3).textScene("WARLOCK-ANAL-ADDICT").textScene("WARLOCK-ANAL-BROKEN").gameEnd(), b.branch(0).textScene("WARLOCK-ANAL-ADDICT").textScene("WARLOCK-ANAL-UNBROKEN")), 
						b.branch("To love the taste of dicks").textScene("WARLOCK-COCK-LOVER"), 
						b.branch("To be a girl").textScene("WARLOCK-FEMINIZATION")
					)
				);

				
				Branch afterDrawer = b.branch().checkScene(
					Stat.PERCEPTION, 
					b.branch(5).textScene("WARLOCK-AVOID-AMBUSH").concat(warlockBattle), 
					b.branch(0).textScene("WARLOCK-PARALYSIS")
				);
				
				Branch afterDiary = b.branch().textScene("WARLOCK-SEE-DRAWER").choiceScene("Check in drawer?", b.branch("Yes").checkScene(CheckType.WEARING_GAUNTLETS, b.branch(true).textScene("WARLOCK-FIND-CRYSTAL").concat(afterDrawer), b.branch(false).textScene("WARLOCK-FIND-PAIN").concat(afterDrawer)), b.branch("No").concat(afterDrawer));
				
				Branch afterAcidCheck = b.branch().textScene("WARLOCK-SEE-DIARY").choiceScene("Read the diary?", b.branch("Yes").textScene("WARLOCK-READ-DIARY").concat(afterDiary), b.branch("No").concat(afterDiary));
				
				Branch laprideEnd = b.branch().textScene("WARLOCK-LAPRIDE-END");
				
				Branch afterRoom = b.branch().textScene("WARLOCK-AFTER-ROOM").choiceScene(
					"Enter the inner sanctum or wait?", 
					b.branch("Enter now").textScene("WARLOCK-SANCTUM").checkScene(
						CheckType.ANY_WILLPOWER, 
						b.branch(true).textScene("WARLOCK-RESIST").choiceScene(
							"Overpower her?", 
							b.branch("Yes").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 6).textScene("WARLOCK-HYPNOSIS-BACKFIRE").choiceScene(
								"Fuck her?", 
								b.branch("Yes").require(ChoiceCheckType.FREE_COCK).textScene("WARLOCK-BOTTOM").choiceScene(
									"How do you want her?", 
									b.branch("On the floor").textScene("WARLOCK-HANDS-AND-KNEES"), 
									b.branch("Sitting in lap").textScene("WARLOCK-LAPRIDE").choiceScene(
										"Free her?", 
										b.branch("Get wet").textScene("WARLOCK-CREAMY-FINISH").concat(laprideEnd), 
										b.branch("Let her make a mess").textScene("WARLOCK-WARDROBE-MALFUNCTION").concat(laprideEnd)
									)
								), 
								b.branch("No").textScene("WARLOCK-BEATEN")
							),
							b.branch("No").textScene("WARLOCK-DAGGER").concat(warlockBattle) 
						), 
						b.branch(false).textScene("WARLOCK-HYPNOSIS") 
					),
					b.branch("Wait and see").textScene("WARLOCK-SANCTUM-WAIT").checkScene(
						Stat.PERCEPTION, 
						b.branch(6).textScene("WARLOCK-AVOID-ACID").concat(afterAcidCheck),
						b.branch(0).checkScene(CheckType.WEARING_SHOES, b.branch(true).textScene("WARLOCK-ACID-PROTECTION").concat(afterAcidCheck), b.branch(false).textScene("WARLOCK-ACID").concat(afterAcidCheck))
					)
				);
				Branch searchRoom = b.branch().textScene("WARLOCK-SEARCH-ROOM").choiceScene("Read the tome?", b.branch("Yes").textScene("WARLOCK-READ-TOME").concat(afterRoom), b.branch("No").concat(afterRoom));
				
				return b.branch().textScene("WARLOCK-INTRO").checkScene(
					CheckType.MANOR_UNSEEN, 
					b.branch(true).textScene("WARLOCK-WARNING"),
					b.branch(false).checkScene(
						CheckType.MANOR_UNVISITED, 
						b.branch(true).textScene("WARLOCK-ENTER").choiceScene(
							"Enter the room?", 
							b.branch("Break the door down").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.STRENGTH, 6).textScene("WARLOCK-BREAK-DOOR").concat(searchRoom), 
							b.branch("Open it with magic").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 4).textScene("WARLOCK-BLAST-DOOR").concat(searchRoom), 
							b.branch("Leave it alone").concat(afterRoom)
						),				
						b.branch(false).checkScene(CheckType.WARLOCK_FEMINIZED, b.branch(true).textScene("WARLOCK-FEM-REPEAT"), b.branch(false).textScene("WARLOCK-REVISIT"))
					)
				);
			case WEAPON_SHOP:
				return b.branch().textScene("WEAPON-SHOP").shopScene(ShopCode.WEAPON_SHOP);
			case WERESLUT:
				Branch knotted = b.branch().textScene("WEREWOLF-KNOT").checkScene(Perk.BITCH, b.branch(3).textScene("WEREWOLF-BITCH-END").gameEnd(), b.branch(0).textScene("WEREWOLF-POST-KNOT"));
				Branch mated = b.branch().textScene("WEREWOLF-MATED").concat(knotted);
				Branch bitch = b.branch(2).textScene("WEREWOLF-BITCH").concat(mated);
				Branch uninterested = b.branch(0).textScene("WEREWOLF-UNINTERESTED");
				Branch defeatNoBitch = b.branch(0).checkScene(CheckType.HAS_TRUDY, b.branch(true).textScene("WEREWOLF-TRUDY"), b.branch(false).concat(uninterested));
				
				Branch[] werewolfBattleOutcomes = new Branch[]{
					b.branch(Outcome.VICTORY).textScene("WEREWOLF-VICTORY").checkScene(CheckType.WEREWOLF_NO_LUST, b.branch(true).checkScene(Perk.BITCH, bitch, uninterested), b.branch(false).textScene("WEREWOLF-STRONG").concat(mated)),
			        b.branch(Outcome.KNOT_ANAL).textScene("WEREWOLF-BATTLE-KNOT").concat(knotted),
			        b.branch(Outcome.KNOT_ORAL).textScene("WEREWOLF-BATTLE-KNOT-ORAL").gameEnd(),
			        b.branch(Outcome.DEFEAT).textScene("WEREWOLF-DEFEAT").checkScene(Perk.BITCH, bitch, defeatNoBitch),
			        b.branch(Outcome.SATISFIED_ANAL).textScene("WEREWOLF-SATISFIED"),
			        b.branch(Outcome.SUBMISSION).textScene("WEREWOLF-SUBMISSION")
				};
				Branch werewolfBattle = b.branch().battleScene(BattleCode.WERESLUT, werewolfBattleOutcomes);
				Branch werewolfStealthBattle = b.branch().battleScene(BattleCode.WERESLUT, werewolfBattleOutcomes).setDelay(2);
				
				Branch werewolfScouted = b.branch().textScene("WEREWOLF-SCOUTED").checkScene(
					CheckType.STEALTH_LEVEL_2, 
					b.branch(true).choiceScene(
						"Fight the werewolf?",
						b.branch("Sneak Attack").textScene("WEREWOLF-FIGHT").concat(werewolfStealthBattle),
						b.branch("Have Trudy Fight").require(ChoiceCheckType.HAS_TRUDY).textScene("WEREWOLF-TRUDY"),
						b.branch("Avoid")
					), 
					b.branch(false).choiceScene(
						"Fight the werewolf?",
						b.branch("Fight").textScene("WEREWOLF-FIGHT").concat(werewolfBattle),
						b.branch("Have Trudy Fight").require(ChoiceCheckType.HAS_TRUDY).textScene("WEREWOLF-TRUDY"),
						b.branch("Avoid")
					)
				);
				
				return b.branch().textScene("WEREWOLF-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2, 
					b.branch(true).concat(werewolfScouted),
					b.branch(false).checkScene(CheckType.STEALTH_LEVEL_2, b.branch(true).concat(werewolfScouted), b.branch(false).textScene("WEREWOLF-ENTRY").concat(werewolfBattle))
				);
			case WEREWOLF_STORY:
				return b.branch().textScene("STORY-WEREWOLF").battleScene(BattleCode.WEREWOLF_STORY, b.branch(Outcome.VICTORY).textScene("STORY-WEREWOLF-VICTORY"), b.branch(Outcome.DEFEAT).textScene("STORY-WEREWOLF-DEFEAT"), b.branch(Outcome.KNOT_ORAL).textScene("WEREWOLF-BATTLE-KNOT-ORAL").gameEnd());
			case WITCH_COTTAGE:
				Branch magicShop = b.branch().textScene("WITCH-COTTAGE-STORE").choiceScene("Peruse her wares?", b.branch("Peruse").shopScene(ShopCode.MAGIC_SHOP), b.branch("Leave"));
				Branch purchase = b.branch().choiceScene(
					"Purchase the goddess' blessing?", 
					b.branch("Pay 100 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 100).textScene("WITCH-COTTAGE-MONEY").concat(magicShop), 
					b.branch("Pay with soulbit").textScene("WITCH-COTTAGE-SOUL").concat(magicShop), 
					b.branch("Give her the gem").require(ChoiceCheckType.HAS_GEM).textScene("WITCH-COTTAGE-GEM").concat(magicShop), 
					b.branch("Don't buy it")
				);
				return b.branch().checkScene(
					CheckType.WITCH_MET, 
					b.branch(true).checkScene(CheckType.BLESSING_PURCHASED, b.branch(true).textScene("WITCH-COTTAGE-RETURN-BOUGHT").concat(magicShop), b.branch(false).textScene("WITCH-COTTAGE-RETURN-BUY").concat(purchase)), 
					b.branch(false).checkScene(
						CheckType.CRIER_KNOWLEDGE, 
						b.branch(true).textScene("WITCH-COTTAGE").concat(purchase), 
						b.branch(false).textScene("WITCH-COTTAGE-NOQUEST")
					)
				);
			default: 
		}
		return b.branch().textScene("TOWN");	
	}
}
