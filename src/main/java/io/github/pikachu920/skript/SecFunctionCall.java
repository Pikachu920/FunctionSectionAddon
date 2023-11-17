package io.github.pikachu920.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.Signature;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import javax.annotation.Nullable;
import java.util.List;

public class SecFunctionCall extends Section {

    static {
        Skript.registerSection(SecFunctionCall.class, "execute function <.+>");
    }

    protected static Object[] lastReturnValue;

    private Function<?> functionToExecute;
    private Expression<?>[] argumentExpressions;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        String functionName = parseResult.regexes.get(0).group();
        functionToExecute = Functions.getGlobalFunction(functionName);
        if (functionToExecute == null) {
            Skript.error("No such function " + functionName);
            return false;
        }
        EntryValidator validator = createSectionValidator(functionToExecute);
        EntryContainer validatedEntries = validator.validate(sectionNode);
        if (validatedEntries == null) {
            return false;
        }
        argumentExpressions = buildArgumentArray(functionToExecute, validatedEntries);
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        if (functionToExecute == null) {
            return "execute function section";
        }
        return "execute function " + functionToExecute.getName();
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        Object[][] arguments = getArguments(event, argumentExpressions);
        lastReturnValue = functionToExecute.execute(arguments);
        return getNext();
    }

    private Object[][] getArguments(Event event, Expression<?>[] argumentExpressions) {
        Object[][] arguments = new Object[argumentExpressions.length][];
        for (int i = 0; i < argumentExpressions.length; i++) {
            Expression<?> argumentExpression = argumentExpressions[i];
            arguments[i] = argumentExpression.getArray(event);
        }
        return arguments;
    }

    private Expression<?>[] buildArgumentArray(Function<?> function, EntryContainer entries) {
        Signature<?> signature = function.getSignature();
        Parameter<?>[] parameters = signature.getParameters();
        Expression<?>[] argumentArray = new Expression<?>[parameters.length];
        int currentIndex = 0;
        for (Parameter<?> parameter : parameters) {
            argumentArray[currentIndex] = (Expression<?>) entries.getOptional(parameter.getName(), true);
            currentIndex++;
        }
        return argumentArray;
    }

    private EntryValidator createSectionValidator(Function<?> function) {
        EntryValidator.EntryValidatorBuilder builder = EntryValidator.builder();
        Signature<?> signature = function.getSignature();
        for (Parameter<?> parameter : signature.getParameters()) {
            ExpressionEntryData<?> parameterEntryData = new ExpressionEntryData<>(
                    parameter.getName(),
                    (Expression<Object>) parameter.getDefaultExpression(),
                    parameter.getDefaultExpression() != null,
                    (Class<Object>) parameter.getType().getC()
            );
            builder.addEntryData(parameterEntryData);
        }
        return builder.build();
    }

}
