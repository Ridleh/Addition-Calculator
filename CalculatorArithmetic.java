import java.math.BigDecimal;

/**
The goal of this project is to make a calculator using only addition. Starting with the Addition method
and working our ways up to creating more and more complex arithmatic operators. For example, with addition
we can create multiplication, which we can then use to create exponents, and so on. 

Operators implemented so far:
Addition 
Subtraction
Multiplication
Division
Exponents
Modulo

@TODO:
[x] (QOL) Switch from double to bigdecimal (double is bad for math...)
[x] (Feature) Redo multiply method to allow multiplication of decimals
[x]  (Bug) Divide method does not work when the numerator is a small decimal and the denominator is a big number
        e.g. 7.03/505, 43.7/888 - it is off by a power of 10^n,
[x]  (Bug) Divide method does not work for negative numbers
[]  (Bug) powerOf method does not work for negative numbers
[]  (Bug) modulo method does not work for negative numbers
[x] (QOL) Refactor and add proper comments to each method

*/

public class CalculatorArithmetic{
 private static final BigDecimal decimalSpaces = new BigDecimal("3");
 private static final BigDecimal zero = new BigDecimal("0");
   
    BigDecimal add(BigDecimal x, BigDecimal y){
        return x.add(y);
    }
    
    BigDecimal subtract(BigDecimal x, BigDecimal y){
        return add(x,y.negate());
    }
    /**
     * Multiply method adds the variable x to itself y times
     * Process is different if y is not an integer
     * If y is not an integer we call multiplyDecimal and return what the method returns
     * @param x - the variable we are modifying
     * @param y - how many times we are adding
     * @return x multiplied by y
     */
    BigDecimal multiply(BigDecimal x, BigDecimal y){
        BigDecimal yAsInt = new BigDecimal( y.intValue() );

        if( subtract(y, yAsInt ).compareTo( zero ) != 0 ) return multiplyDecimal(x,y);
        BigDecimal result = new BigDecimal("0");
        for(BigDecimal loopCount = new BigDecimal("0"); loopCount.compareTo( y.abs() ) < 0 ;loopCount = add(loopCount,new BigDecimal("1")) ){
            result = add(result, x);
        }
        return (y.compareTo(zero) < 0) ? result.negate() : result;
    }
    /**
     * Divide method subtracts x from itself y times
     * If y is zero we throw an exception
     * If x is smaller than y we call divideSmallNum and return what the method returns
     * After finding the quotient if there is a remainder we call remainderToDecimal. 
     * We concatonate the quotient and remainder together 
     * @param x - variable we are modifying
     * @param y - how many times we subtract
     * @return x divided by y
     */
    BigDecimal divide(BigDecimal x, BigDecimal y){
        BigDecimal absX = x.abs();
        BigDecimal absY = y.abs();

        if( y.compareTo(zero) == 0 ) throw new IllegalArgumentException("Cannot divide by 0!");
        else if ( absX.compareTo(absY) < 0 ) return divideSmallNum(x,y);   
        BigDecimal quotient = new BigDecimal("0");
        while(absX.compareTo(absY) >= 0){
            absX = subtract(absX,absY);
            quotient = add(quotient, new BigDecimal("1"));
        }
        if(absX.compareTo(zero) != 0 ){ 
            BigDecimal saveQuotient = (x.compareTo(zero) < 0) ? quotient.negate() : quotient; 
            saveQuotient = (y.compareTo(zero) < 0) ? quotient.negate() : quotient;
            BigDecimal decimal = remainderToDecimal(absX,absY);
            return createDecimal(saveQuotient,decimal);
        }
        else{
            quotient = (x.compareTo(zero) < 0) ? quotient.negate() : quotient; 
            return (y.compareTo(zero) < 0) ? quotient.negate() : quotient;
        }
    }
    /**
     * Raises a number to a power
     * @param x - base number
     * @param y - the exponent
     * @return x to the power of y
     */
    BigDecimal powerOf(BigDecimal x, BigDecimal y){
        BigDecimal result = new BigDecimal("1");

        for(BigDecimal loopCount = new BigDecimal("0"); loopCount.compareTo( y.abs() ) < 0 ;loopCount = add(loopCount,new BigDecimal("1")) ){
            result = multiply(result, x);
        }
        return result;
    }
    /**
     * Finds the mod(remainder) of a number
     * If y is zero we throw an exception
     * @param x - base number
     * @param y - what we mod by
     * @return x % y
     */
    BigDecimal modulo(BigDecimal x, BigDecimal y){
        if( y.compareTo(zero) == 0 ) throw new IllegalArgumentException("Cannot mod by 0!");
        while(x.compareTo(y) >= 0){
            x = subtract(x,y);
        }
        return x;
    }
    /**
     * called from the divide method. multiplies the remainder by 1000
     * And divides the result by the denominator passed from the divide method
     * The quotient of this number is the remainder for the number divide is working on
     * @param x - remainder from the divide method
     * @param y - what we are dividing x by
     * @return the remainder of the number divide method is working on
     */
    BigDecimal remainderToDecimal(BigDecimal x, BigDecimal y){
        BigDecimal powerOf = powerOf(new BigDecimal("10"),decimalSpaces);
        BigDecimal quotient = new BigDecimal("0");

        x = multiply(x,powerOf);
        while(x.compareTo(y) >= 0){
            x = subtract(x,y);
            quotient = add(quotient, new BigDecimal("1"));
        }
        return quotient;
    }
    /**
     * we concatenate the quotient and remainder from divide method together 
     * @param x - the quotient
     * @param y - the remainder
     * @return x + "." + y 
     */
    BigDecimal createDecimal(BigDecimal x, BigDecimal y){
        String numberToString = x.intValue() + "." + y.intValue();
        BigDecimal stringToDecimal = new BigDecimal(numberToString);
        return stringToDecimal;
    }
    /**
     * called from the multiply method
     * multiplies y by 10 until y is an integer, then multiples x by y
     * then divides x by 10 times how many times we multiplied y
     * @param x - number we are modifying
     * @param y - number we convert to int, as well as what me multiply x by
     * @return x multiplied by y
     */
    BigDecimal multiplyDecimal(BigDecimal x, BigDecimal y){
        BigDecimal timesMultiplied = new BigDecimal("0");
        BigDecimal result = new BigDecimal("0");

        while( subtract( y, new BigDecimal( y.intValue() ) ).compareTo(zero) != 0 ){
            y = multiply(y, new BigDecimal("10"));
            timesMultiplied = add(timesMultiplied, new BigDecimal("1"));
        }
        BigDecimal factorOf = powerOf(new BigDecimal("10"), timesMultiplied);
        result = multiply(x,y);
        result = divide(result,factorOf);
        return (y.compareTo(zero) < 0) ? result.negate() : result;

    }
    /**
     * called from the divide method
     * multiplies x by 1000 then divides by y
     * we shift the decimal 3 places by turning the number into a string
     * adding a new period and removing the old one, and converting
     * back into a big decimal
     * @param x - number we are modifying
     * @param y - number we are dividing x by
     * @return x divided by y
     */
    BigDecimal divideSmallNum(BigDecimal x, BigDecimal y){
        BigDecimal absX = x.abs();
        BigDecimal absY = y.abs();
        BigDecimal powerOf = powerOf(new BigDecimal("10"),decimalSpaces);

        absX = multiply( absX, powerOf);
        String numToString =  divide(absX,absY) + "";
        if( numToString.indexOf(".") < 3){
            String fillZeroes = "";
            for(int i = 0; i < numToString.indexOf("."); i++){
                fillZeroes += "0";
            }
            numToString = fillZeroes+numToString;
        } 
        int periodIndex = numToString.indexOf(".") - 3;
        numToString = numToString.replaceAll("\\.", "");
        numToString = numToString.substring(0,periodIndex) + "." + numToString.substring(periodIndex);
        BigDecimal result = new BigDecimal(numToString);
        result = (x.compareTo(zero) < 0) ? result.negate() : result; 
        return (y.compareTo(zero) < 0) ? result.negate() : result;    
   }
}