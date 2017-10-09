package seedu.address.logic.commands;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.*;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

import java.util.List;
import java.util.Set;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

public class BirthdayCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "birthday";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Updates the person's birthday identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UPDATE_PERSON_BIRTHDAY_SUCCESS = "Updated Person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index targetIndex;

    private final Birthday birthday;

    public BirthdayCommand(Index targetIndex, Birthday birthday) {
        this.targetIndex = targetIndex;
        this.birthday = birthday;
    }

    private Person updatePersonBirthday(ReadOnlyPerson personToUpdateBirthday, Birthday birthday) {
        Name name = personToUpdateBirthday.getName();
        Phone phone = personToUpdateBirthday.getPhone();
        Email email = personToUpdateBirthday.getEmail();
        Address address = personToUpdateBirthday.getAddress();
        Set<Tag> tags = personToUpdateBirthday.getTags();

        Person personUpdated = new Person(name, phone, email, address, birthday, tags);

        return personUpdated;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToUpdateBirthday = lastShownList.get(targetIndex.getZeroBased());
        Person personUpdated = updatePersonBirthday(personToUpdateBirthday, birthday);

        try {
            model.updatePerson(personToUpdateBirthday, personUpdated);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(String.format(MESSAGE_UPDATE_PERSON_BIRTHDAY_SUCCESS, personToUpdateBirthday));
    }
}
