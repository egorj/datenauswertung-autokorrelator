
from matplotlib import pyplot as plt
import time

dir = "./inputdata/"
filePrefix = "out"
fileSuffix = ".txt"

startNum = 0
endNum = 9


class FileViewer():

  if __name__ == '__main__':
    plt.ion()
    fig = plt.figure()
    fignum = fig.number
    axXY = fig.add_subplot( 111 )
    lineMov, = axXY.plot( [], [] )
    lineUp, = axXY.plot( [], [] )
    lineFWHM, = axXY.plot( [], [] )
    plt.pause( 0.05 )

    currNum = startNum

    while True:
      fileName = dir + filePrefix + str( currNum ) + fileSuffix
      with open( fileName, "r" ) as fp:
        lines = fp.readlines()

      numComments = len( [ l for l in lines if "#" in l ] )
      size = len( lines ) - numComments

      movX = [ 0 ] * size
      movY = [ 0 ] * size
      upY = [ 0 ] * size
      fwhmLine = [ None ] * size

      fwhmStr = [ l for l in lines if "FWHM" in l ][ 0 ].split( "=" )[ 1 ]
      valsStr = fwhmStr.split( "," )
      fwhm = float( valsStr[ 0 ] )
      L = int( valsStr[ 1 ] )
      R = int( valsStr[ 2 ] )

      for i, l in enumerate( lines[ numComments : ] ):
        valsStr = l.split( "\t" )
        movX[ i ] = float( valsStr[ 0 ] )
        movY[ i ] = float( valsStr[ 1 ] )
        upY[ i ] = float( valsStr[ 2 ] )

      # linear interpolation
      for i in range( L, R ):
        fwhmLine[ i ] = ( upY[ R ] - upY[ L ] ) / ( R - L ) * ( i - L ) + upY[ L ]

      lineMov.set_data( movX, movY )
      lineUp.set_data( movX, upY )
      lineFWHM.set_data( movX, fwhmLine )

      axXY.relim()
      axXY.autoscale_view( True, True, True )

      axXY.set_title( "FWHM = " + str( fwhm ) )

      if not plt.fignum_exists( fignum ):
        break

      plt.pause( 0.05 )
      time.sleep( 0.05 )

      currNum += 1
      currNum = ( currNum - startNum ) % ( endNum - startNum + 1 ) + startNum

