package com.alwa.spread;

import java.math.RoundingMode;
import java.util.function.Function;

public abstract class StepFunctionResolver {

    abstract Function<Object, Object> getStepFunction(int totalSteps, int currentStep, Object example, RoundingMode roundingMode);
}
