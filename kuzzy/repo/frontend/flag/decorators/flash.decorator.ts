import * as chalk from 'chalk';
import { FlashService } from '../../capture/services/flash.service';
import { SystemState } from '../../system/models/system-state.model';
import { TestCaseService } from '../../capture/services/test-case.service';
import { GLOBAL } from '../../init/const/global.const';
import { CoverageService } from '../../capture/services/coverage.service';

// TODO : calls from Out of System
// TODO : add expects() for creation of other instances


const DEBUG_LOG_DECORATOR = false;

export function Flash(methodInstancePath: string, isMethodDefinedInOutOfSystemClassOrInterface: boolean) {
    return function (target: any, methodName: string, descriptor: PropertyDescriptor) {
        let originalMethod = descriptor.value;
        descriptor.value = function (...args: any[]) {
            const callingInstancePath: string = args[0];
            logWrapper(this.constructor?.name, methodName, args, descriptor);
            if (isExternalCall(callingInstancePath, methodInstancePath)) {
                flashAndAddMockDependencies(this, args);
            }
            const systemStateBeforeCall = GLOBAL.lastSystemState;
            const argumentsToApply: any[] = getArgumentsToApply(args, isMethodDefinedInOutOfSystemClassOrInterface);
            let returnedValue: any = originalMethod.apply(this, argumentsToApply);
            if (increasesCoverage(callingInstancePath, methodInstancePath)) {
                increaseCoverage(returnedValue, methodName, this, systemStateBeforeCall, methodInstancePath, args);
            }
            return returnedValue;
        }
    }
}


function flashAndAddMockDependencies(context: any, argts: any[]) {
    FlashService.superFlash();
    FlashService.addMockDependencies(context, argts);
}


function isExternalCall(callingInstancePath: string, methodInstancePath: string): boolean {
    return callingInstancePath !== methodInstancePath;
}


function increasesCoverage(callingInstancePath: string, methodInstancePath: string): boolean {
    return isExternalCall(callingInstancePath, methodInstancePath) && CoverageService.hasParsedStatements(methodInstancePath);
}


function increaseCoverage(returnedValue: any, methodName: string, context: any, systemStateBeforeCall: SystemState, methodInstancePath: string, argts: any[]): void {
    if (returnedValue?.constructor.name === 'Promise') {
        (returnedValue as Promise<any>).then(result => {
            secondFlash(methodName, context, result, true, systemStateBeforeCall, methodInstancePath, argts.slice(1));
        });
    } else {
        secondFlash(methodName, context, returnedValue, false, systemStateBeforeCall, methodInstancePath, argts.slice(1));
    }
}


// TODO: Add ClassEnums for method parameters
function secondFlash(methodName: string, thisInstance: any, returnedValue: any, isAsync: boolean, systemStateBeforeCall: SystemState, methodInstancePath: string, methodArguments: any[]): void {
    const systemStateAfterFlash: SystemState = FlashService.superFlash();
    TestCaseService.create(methodName, thisInstance, returnedValue, systemStateBeforeCall, systemStateAfterFlash, methodInstancePath, methodArguments);
    CoverageService.cover(methodInstancePath);
}


/**
 * If the method is defined out of system, it can't be called with the parameter 'callingInstancePath' added during flag process.
 * This method removes this additional argument in this case.
 * @param argumentsIncludingFlagArgument
 * @param isMethodDefinedInOutOfSystemClassOrInterface
 */
function getArgumentsToApply(argumentsIncludingFlagArgument: any[], isMethodDefinedInOutOfSystemClassOrInterface: boolean): any[] {
    return isMethodDefinedInOutOfSystemClassOrInterface ? argumentsIncludingFlagArgument.slice(1) : argumentsIncludingFlagArgument;
}


function logWrapper(className: string, methodName: string, args: any, descriptor: PropertyDescriptor): void {
    if (DEBUG_LOG_DECORATOR) {
        console.log(chalk.magentaBright('WRAPPER DECORATOR'));
        console.log(chalk.blueBright('Target : '), className);
        console.log(chalk.blueBright('Property : '), methodName);
        console.log(chalk.blueBright('Args'), args);
        console.log(chalk.blueBright('Descriptor'), descriptor.value);
    }
}
